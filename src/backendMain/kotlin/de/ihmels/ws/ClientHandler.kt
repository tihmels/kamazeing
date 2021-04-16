package de.ihmels.ws

import de.ihmels.CMessageType
import de.ihmels.CMessageType.*
import de.ihmels.Entities
import de.ihmels.Logging
import de.ihmels.SMessageType.*
import de.ihmels.logger
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.toList
import de.ihmels.skippable.GeneratorStateFlow
import de.ihmels.skippable.SolverStateFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ClientHandler(private val client: Client) : Logging, ClientMessageHandler {

    private val log = logger()

    private val supervisorJob = SupervisorJob()
    private var generatorJob: Job? = null
    private var solverJob: Job? = null

    private val scope = CoroutineScope(supervisorJob + Dispatchers.Default)

    private val _store = MutableStateFlow(ClientState())
    private val store = _store.asStateFlow()

    private val generatorManager = GeneratorStateFlow()
    private val solverManager = SolverStateFlow()

    suspend fun start() {

        try {

            for (message in client.input) {
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
            is UpdateMazeProperties -> updateProperties(cMessage)
            is GeneratorAction.Generate -> generate(cMessage)
            is SolverAction.Solve -> solve(cMessage)
            else -> {
                throw IllegalStateException()
            }
        }

    private suspend fun sendGeneratorAlgorithms() = client.send(Generators(Entities(Generator.toEntities())))

    private suspend fun sendSolverAlgorithms() = client.send(Solvers(Entities(Solver.toEntities())))

    private suspend fun resetMaze() = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)

        _store.value = newState

        client.send(ResetMaze(newState.maze.toDto()))

    }

    private suspend fun updateProperties(message: UpdateMazeProperties) = clearScope(scope) {

        val newState = Intent.UpdateMazeProperties(message.properties).reduce(_store.value)

        _store.value = newState

        client.send(UpdateMaze(newState.maze.toDto()))

    }

    private suspend fun generate(message: GeneratorAction.Generate) = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)

        _store.value = newState

        val flow = generatorManager.generate(newState.maze, message.generatorId)

        generatorJob = flow
            .delay(100)
            .onEach {
                _store.value = Intent.UpdateMaze(it).reduce(_store.value)
                client.send(UpdateMaze(it.toDto()))
            }
            .launchIn(scope)

    }

    private suspend fun solve(message: SolverAction.Solve) = clearScope(scope) {

        val state = store.value

        if (state.initialized) {

            val flow = solverManager.solve(message.solverId, state.maze)

            solverJob = flow
                .mapNotNull { it?.toList() }
                .delay(100)
                .onEach {
                    client.send(UpdatePath(it))
                }
                .launchIn(scope)

        }

    }

    init {

        generatorManager.state.onEach {
            client.send(UpdateGeneratorState(it))
        }.launchIn(scope + Job())

        solverManager.state.onEach {
            client.send(UpdateSolverState(it))
        }.launchIn(scope + Job())

    }

}

suspend fun clearScope(scope: CoroutineScope, block: suspend () -> Unit) {
    scope.coroutineContext.cancelChildren()
    block()
}

fun <T> Flow<T>.delay(time: Long): Flow<T> = flow {
    collect { value ->
        kotlinx.coroutines.delay(time)
        emit(value)
    }
}

suspend fun Job.cancelAndJoin(cause: CancellationException? = null) {
    cancel(cause)
    return join()
}
