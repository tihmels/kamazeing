package de.ihmels.ws

import de.ihmels.*
import de.ihmels.CMessageType.*
import de.ihmels.SMessageType.*
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.toList
import de.ihmels.skippable.GeneratorStateFlow
import de.ihmels.skippable.SolverStateFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KMutableProperty0

class ClientHandler(private val client: Client) : Logging, ClientMessageHandler {

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

    override suspend fun handle(cMessage: CMessageType) =
        when (cMessage) {
            GetGeneratorAlgorithms -> sendGeneratorAlgorithms()
            GetSolverAlgorithms -> sendSolverAlgorithms()
            ResetMazeGrid -> resetMaze()
            is UpdateMazeProperties -> updateMazeProperties(cMessage.properties)
            is GeneratorAction.Generate -> generate(cMessage)
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
        solverDelay = when (speed) {
            1 -> 300L
            2 -> 200L
            3 -> 100L
            else -> 200L
        }
    }

    private fun setGeneratorSpeed(speed: Int) {
        generatorDelay = when (speed) {
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

        generator.execute(newState.maze, message.generatorId) {
            this.delay(::generatorDelay)
                .onEach { maze ->
                    _store.value = Intent.UpdateMaze(maze).reduce(_store.value)
                    client.send(UpdateMaze(maze.toDto()))
                }
        }

    }

    private suspend fun solve(message: SolverAction.Solve) = clearScope(scope) {

        val currentState = store.value

        if (currentState.initialized) {

            solver.execute(currentState.maze, message.solverId) {
                this.delay(::solverDelay)
                    .filterNotNull()
                    .onEach {
                        _store.value = Intent.UpdatePath(it).reduce(_store.value)
                        client.send(UpdatePath(it.toList()))
                    }
            }

        }

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

suspend fun Job.cancelAndJoin(cause: CancellationException? = null) {
    cancel(cause)
    return join()
}