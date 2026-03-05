package de.ihmels.ws

import de.ihmels.*
import de.ihmels.RequestMessageType.*
import de.ihmels.ResponseMessageType.*
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.toList
import de.ihmels.skippable.GeneratorStateFlow
import de.ihmels.skippable.SolverStateFlow
import de.ihmels.utils.SpeedConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KMutableProperty0

class ClientHandler(private val client: Client) : Logging {

    private val log = logger()

    private val supervisorJob = SupervisorJob()

    private val scope = CoroutineScope(supervisorJob + Dispatchers.Default)

    private val _store = MutableStateFlow(ClientState())
    private val store = _store.asStateFlow()

    private val generator = GeneratorStateFlow(scope)
    private val solver = SolverStateFlow(scope)

    private var generatorDelay: Long = 150L
    private var solverDelay: Long = 150L

    suspend fun start() {

        try {

            for (message in client.input) {
                log.info("Incoming message: ${message.messageType}")
                handle(message.messageType)
            }

        } finally {
            scope.cancel()
        }

    }

    private suspend fun handle(cMessage: RequestMessageType) =
        when (cMessage) {
            GetGeneratorAlgorithms -> sendGeneratorAlgorithms()
            GetSolverAlgorithms -> sendSolverAlgorithms()
            ResetMazeGrid -> resetMaze()
            is UpdateMazeProperties -> updateMazeProperties(cMessage.properties)
            is GeneratorAction.Generate -> generate(cMessage)
            is GeneratorAction.CompareGenerators -> compareGenerators(cMessage)
            is GeneratorAction.Cancel -> generator.cancel()
            is GeneratorAction.SetSpeed -> setGeneratorSpeed(cMessage.speed)
            RequestMessageType.SkipGenerator -> generator.skip()
            is SolverAction.Solve -> solve(cMessage)
            is SolverAction.Cancel -> solver.cancel()
            is SolverAction.SetSpeed -> setSolverSpeed(cMessage.speed)
            RequestMessageType.SkipSolver -> solver.skip()
        }

    private suspend fun sendGeneratorAlgorithms() =
        client.send(Generators(AlgorithmOptions(Generator.toEntities(), Generator.default().id)))

    private suspend fun sendSolverAlgorithms() =
        client.send(Solvers(AlgorithmOptions(Solver.toEntities(), Solver.default().id)))

    private fun setSolverSpeed(speed: Int) {
        solverDelay = SpeedConverter.speedLevelToDelay(speed)
    }

    private fun setGeneratorSpeed(speed: Int) {
        generatorDelay = SpeedConverter.speedLevelToDelay(speed)
    }

    private suspend fun resetMaze() = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)

        _store.value = newState

        client.send(ResetMaze(newState.maze.toDto()))

    }

    private suspend fun updateMazeProperties(properties: MazeProperties) = clearScope(scope) {

        var updatedState = Intent.UpdateMazeProperties(properties).reduce(_store.value)

        if (properties.initializer > -1) {
            updatedState = Intent.ResetMaze.reduce(updatedState)

            val flow = generator.getRawFlow(updatedState.maze, properties.initializer)
            val finalMaze = flow.toList().last()

            updatedState = Intent.UpdateMaze(finalMaze).reduce(updatedState)
        }

        _store.value = updatedState

        client.send(UpdateMaze(updatedState.maze.toDto()))

    }

    private suspend fun generate(message: GeneratorAction.Generate) = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)
        _store.value = newState

        var totalTime: Long = 0
        var lastProgressUpdate = 0L

        generator.execute(newState.maze, message.generatorId) {
            val startTime = System.currentTimeMillis()

            this.delay(::generatorDelay)
                .onEach { maze ->
                    totalTime = System.currentTimeMillis() - startTime
                    lastProgressUpdate = ProgressReporter.reportProgress(maze, totalTime, lastProgressUpdate, client)

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        val finalMaze = _store.value.maze
        client.send(
            UpdateStatistics(
                ProgressReporter.createStatistics(
                    algorithmName = "Generator ${message.generatorId}",
                    durationMs = totalTime,
                    maze = finalMaze,
                    algorithmType = "generator"
                )
            )
        )

    }

    private suspend fun solve(message: SolverAction.Solve) = clearScope(scope) {

        val currentState = store.value

        if (currentState.initialized) {

            var totalTime: Long = 0
            var pathLength = 0
            var visitedCount = 0
            var lastProgressUpdate = 0L

            solver.execute(currentState.maze, message.solverId) {
                val startTime = System.currentTimeMillis()

                this.delay(::solverDelay)
                    .filterNotNull()
                    .onEach { path ->
                        totalTime = System.currentTimeMillis() - startTime
                        visitedCount++
                        pathLength = path.toList().size

                        if (totalTime - lastProgressUpdate >= 200) {
                            val totalCells = currentState.maze.size
                            val percentComplete = (visitedCount.toDouble() / totalCells) * 100

                            client.send(
                                UpdateProgress(
                                    ProgressData(
                                        cellsProcessed = visitedCount,
                                        totalCells = totalCells,
                                        percentComplete = percentComplete,
                                        elapsedMs = totalTime
                                    )
                                )
                            )

                            lastProgressUpdate = totalTime
                        }

                        _store.value = Intent.UpdatePath(path).reduce(_store.value)
                        client.send(UpdatePath(path.toList()))
                    }
            }

            client.send(
                UpdateStatistics(
                    ProgressReporter.createStatistics(
                        algorithmName = "Solver ${message.solverId}",
                        durationMs = totalTime,
                        maze = currentState.maze,
                        pathLength = pathLength,
                        algorithmType = "solver"
                    )
                )
            )

        }

    }

    private suspend fun compareGenerators(message: GeneratorAction.CompareGenerators) = clearScope(scope) {

        val generators = Generator.values()
        val gen1Name = generators.find { it.id == message.generator1Id }?.name ?: "Generator ${message.generator1Id}"
        val gen2Name = generators.find { it.id == message.generator2Id }?.name ?: "Generator ${message.generator2Id}"

        var totalTime1 = 0L
        var lastProgressUpdate1 = 0L
        val newState = Intent.ResetMaze.reduce(_store.value)
        _store.value = newState

        generator.execute(newState.maze, message.generator1Id) {
            val startTime = System.currentTimeMillis()

            this.delay(::generatorDelay)
                .onEach { maze ->
                    totalTime1 = System.currentTimeMillis() - startTime
                    lastProgressUpdate1 = ProgressReporter.reportProgress(maze, totalTime1, lastProgressUpdate1, client)

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        val stats1 = ProgressReporter.createStatistics(
            algorithmName = gen1Name,
            durationMs = totalTime1,
            maze = _store.value.maze,
            algorithmType = "generator"
        )

        var totalTime2 = 0L
        var lastProgressUpdate2 = 0L
        val resetState = Intent.ResetMaze.reduce(_store.value)
        _store.value = resetState

        generator.execute(resetState.maze, message.generator2Id) {
            val startTime = System.currentTimeMillis()

            this.delay(::generatorDelay)
                .onEach { maze ->
                    totalTime2 = System.currentTimeMillis() - startTime
                    lastProgressUpdate2 = ProgressReporter.reportProgress(maze, totalTime2, lastProgressUpdate2, client)

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        val stats2 = ProgressReporter.createStatistics(
            algorithmName = gen2Name,
            durationMs = totalTime2,
            maze = _store.value.maze,
            algorithmType = "generator"
        )

        val winner = when {
            stats1.efficiency != stats2.efficiency -> if (stats1.efficiency > stats2.efficiency) gen1Name else gen2Name
            totalTime1 != totalTime2 -> if (totalTime1 < totalTime2) gen1Name else gen2Name
            else -> ""
        }

        client.send(
            UpdateComparison(
                ComparisonResult(
                    algorithm1 = gen1Name,
                    algorithm2 = gen2Name,
                    stats1 = stats1,
                    stats2 = stats2,
                    winner = winner
                )
            )
        )

    }

    init {

        generator.state.onEach {
            client.send(UpdateGeneratorState(it))
        }.launchIn(scope + Job())

        solver.state.onEach {
            client.send(UpdateSolverState(it))
        }.launchIn(scope + Job())

    }

}

suspend fun clearScope(scope: CoroutineScope, block: suspend () -> Unit) {
    scope.coroutineContext.cancelChildren()
    block()
}

fun <T> Flow<T>.delay(time: KMutableProperty0<Long>): Flow<T> = flow {
    collect { value ->
        delay(time.get())
        emit(value)
    }
}

