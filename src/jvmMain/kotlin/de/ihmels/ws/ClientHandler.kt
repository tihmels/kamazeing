package de.ihmels.ws

import de.ihmels.*
import de.ihmels.CMessageType.*
import de.ihmels.SMessageType.*
import kotlin.system.measureTimeMillis
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.toList
import de.ihmels.skippable.GeneratorStateFlow
import de.ihmels.skippable.SolverStateFlow
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

    private suspend fun handle(cMessage: CMessageType) =
        when (cMessage) {
            GetGeneratorAlgorithms -> sendGeneratorAlgorithms()
            GetSolverAlgorithms -> sendSolverAlgorithms()
            ResetMazeGrid -> resetMaze()
            is UpdateMazeProperties -> updateMazeProperties(cMessage.properties)
            is GeneratorAction.Generate -> generate(cMessage)
            is GeneratorAction.CompareGenerators -> compareGenerators(cMessage)
            is GeneratorAction.Cancel -> generator.cancel()
            is GeneratorAction.SetSpeed -> setGeneratorSpeed(cMessage.speed)
            is SolverAction.Solve -> solve(cMessage)
            is SolverAction.Cancel -> solver.cancel()
            is SolverAction.SetSpeed -> setSolverSpeed(cMessage.speed)
        }

    private suspend fun sendGeneratorAlgorithms() =
        client.send(Generators(Entities(Generator.toEntities(), Generator.default().id)))

    private suspend fun sendSolverAlgorithms() =
        client.send(Solvers(Entities(Solver.toEntities(), Solver.default().id)))

    private fun setSolverSpeed(speed: Int) {
        solverDelay = speedToDelay(speed)
    }

    private fun setGeneratorSpeed(speed: Int) {
        generatorDelay = speedToDelay(speed)
    }

    private companion object {
        fun speedToDelay(speed: Int): Long = when (speed) {
            1 -> 300L
            2 -> 200L
            3 -> 100L
            else -> 200L
        }
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

        generator.execute(newState.maze, message.generatorId) {

            val startTime = System.currentTimeMillis()
            var lastProgressUpdate = 0L

            this.delay(::generatorDelay)
                .onEach { maze ->

                    totalTime = System.currentTimeMillis() - startTime

                    // Emit progress updates (throttle to every 200ms to avoid overloading WebSocket)
                    if (totalTime - lastProgressUpdate >= 200) {
                        val visitedCells = maze.cells.count { !it.isClosed() }
                        val totalCells = maze.cells.size
                        val percentComplete = (visitedCells.toDouble() / totalCells) * 100

                        client.send(
                            UpdateProgress(
                                ProgressData(
                                    cellsProcessed = visitedCells,
                                    totalCells = totalCells,
                                    percentComplete = percentComplete,
                                    elapsedMs = totalTime
                                )
                            )
                        )

                        lastProgressUpdate = totalTime
                    }

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        // Emit final statistics
        val finalState = _store.value
        val finalMaze = finalState.maze
        val visitedCells = finalMaze.cells.count { !it.isClosed() }
        val totalCells = finalMaze.cells.size
        val efficiency = (visitedCells.toDouble() / totalCells) * 100

        client.send(
            UpdateStatistics(
                StatisticsData(
                    algorithmName = "Generator ${message.generatorId}",
                    durationMs = totalTime,
                    cellsProcessed = visitedCells,
                    pathLength = 0,
                    efficiency = efficiency,
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

            solver.execute(currentState.maze, message.solverId) {

                val startTime = System.currentTimeMillis()
                var lastProgressUpdate = 0L

                this.delay(::solverDelay)
                    .filterNotNull()
                    .onEach { path ->

                        totalTime = System.currentTimeMillis() - startTime
                        visitedCount++
                        pathLength = path?.toList()?.size ?: 0

                        // Emit progress updates (throttle to every 200ms)
                        if (totalTime - lastProgressUpdate >= 200) {
                            val totalCells = currentState.maze.cells.size
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

            // Emit final statistics
            val totalCells = currentState.maze.cells.size
            val efficiency = (visitedCount.toDouble() / totalCells) * 100

            client.send(
                UpdateStatistics(
                    StatisticsData(
                        algorithmName = "Solver ${message.solverId}",
                        durationMs = totalTime,
                        cellsProcessed = visitedCount,
                        pathLength = pathLength,
                        efficiency = efficiency,
                        algorithmType = "solver"
                    )
                )
            )

        }

    }

    private suspend fun compareGenerators(message: GeneratorAction.CompareGenerators) = clearScope(scope) {

        // Get generator names for stats
        val generators = Generator.values()
        val gen1Name = generators.find { it.id == message.generator1Id }?.name ?: "Generator ${message.generator1Id}"
        val gen2Name = generators.find { it.id == message.generator2Id }?.name ?: "Generator ${message.generator2Id}"

        var totalTime1 = 0L
        var visitedCells1 = 0
        var totalCells1 = 0
        var efficiency1 = 0.0

        // Run first generator
        val newState = Intent.ResetMaze.reduce(_store.value)
        _store.value = newState

        generator.execute(newState.maze, message.generator1Id) {
            val startTime = System.currentTimeMillis()
            var lastProgressUpdate = 0L

            this.delay(::generatorDelay)
                .onEach { maze ->

                    totalTime1 = System.currentTimeMillis() - startTime

                    // Throttle progress updates
                    if (totalTime1 - lastProgressUpdate >= 200) {
                        visitedCells1 = maze.cells.count { !it.isClosed() }
                        totalCells1 = maze.cells.size
                        val percentComplete = (visitedCells1.toDouble() / totalCells1) * 100

                        client.send(
                            UpdateProgress(
                                ProgressData(
                                    cellsProcessed = visitedCells1,
                                    totalCells = totalCells1,
                                    percentComplete = percentComplete,
                                    elapsedMs = totalTime1
                                )
                            )
                        )

                        lastProgressUpdate = totalTime1
                    }

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        // Collect stats for first algorithm
        visitedCells1 = _store.value.maze?.cells?.count { !it.isClosed() } ?: 0
        totalCells1 = _store.value.maze?.cells?.size ?: 1
        efficiency1 = (visitedCells1.toDouble() / totalCells1) * 100

        val stats1 = StatisticsData(
            algorithmName = gen1Name,
            durationMs = totalTime1,
            cellsProcessed = visitedCells1,
            pathLength = 0,
            efficiency = efficiency1,
            algorithmType = "generator"
        )

        var totalTime2 = 0L
        var visitedCells2 = 0
        var totalCells2 = 0
        var efficiency2 = 0.0

        // Run second generator
        val resetState = Intent.ResetMaze.reduce(_store.value)
        _store.value = resetState

        generator.execute(resetState.maze, message.generator2Id) {
            val startTime = System.currentTimeMillis()
            var lastProgressUpdate = 0L

            this.delay(::generatorDelay)
                .onEach { maze ->

                    totalTime2 = System.currentTimeMillis() - startTime

                    // Throttle progress updates
                    if (totalTime2 - lastProgressUpdate >= 200) {
                        visitedCells2 = maze.cells.count { !it.isClosed() }
                        totalCells2 = maze.cells.size
                        val percentComplete = (visitedCells2.toDouble() / totalCells2) * 100

                        client.send(
                            UpdateProgress(
                                ProgressData(
                                    cellsProcessed = visitedCells2,
                                    totalCells = totalCells2,
                                    percentComplete = percentComplete,
                                    elapsedMs = totalTime2
                                )
                            )
                        )

                        lastProgressUpdate = totalTime2
                    }

                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

        // Collect stats for second algorithm
        visitedCells2 = _store.value.maze?.cells?.count { !it.isClosed() } ?: 0
        totalCells2 = _store.value.maze?.cells?.size ?: 1
        efficiency2 = (visitedCells2.toDouble() / totalCells2) * 100

        val stats2 = StatisticsData(
            algorithmName = gen2Name,
            durationMs = totalTime2,
            cellsProcessed = visitedCells2,
            pathLength = 0,
            efficiency = efficiency2,
            algorithmType = "generator"
        )

        // Determine winner (by efficiency, then by time)
        val winner = when {
            efficiency1 != efficiency2 -> if (efficiency1 > efficiency2) gen1Name else gen2Name
            totalTime1 != totalTime2 -> if (totalTime1 < totalTime2) gen1Name else gen2Name
            else -> ""
        }

        // Send comparison result
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

