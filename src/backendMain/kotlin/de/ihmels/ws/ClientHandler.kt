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
    private var generatorJob: Job? = null
    private var solverJob: Job? = null

    private val scope = CoroutineScope(supervisorJob + Dispatchers.Default)

    private val _store = MutableStateFlow(ClientState())
    private val store = _store.asStateFlow()

    private val generatorManager = GeneratorStateFlow()
    private val solverManager = SolverStateFlow()

    private var generatorDelay = 300L
    private var solverDelay = 300L

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
            is UpdateMazeProperties -> updateMazeProperties(cMessage.properties)
            is GeneratorAction.Generate -> generate(cMessage)
            is GeneratorAction.Cancel -> cancelGenerator()
            is GeneratorAction.SetSpeed -> setGeneratorSpeed(cMessage.speed)
            is SolverAction.Solve -> solve(cMessage)
            is SolverAction.Cancel -> cancelSolver()
            is SolverAction.SetSpeed -> setSolverSpeed(cMessage.speed)
            else -> {
                throw IllegalStateException()
            }
        }

    private fun cancelSolver() {
        solverJob?.cancel()
    }

    private fun setSolverSpeed(speed: Int) {
        when (speed) {
            1 -> solverDelay = 300L
            2 -> solverDelay = 150L
            3 -> solverDelay = 50L
        }
    }

    private fun setGeneratorSpeed(speed: Int) {
        when (speed) {
            1 -> generatorDelay = 300L
            2 -> generatorDelay = 150L
            3 -> generatorDelay = 50L
        }
    }

    private fun cancelGenerator() {
        generatorJob?.cancel()
    }

    private suspend fun sendGeneratorAlgorithms() =
        client.send(Generators(Entities(Generator.toEntities(), Generator.default().id)))

    private suspend fun sendSolverAlgorithms() =
        client.send(Solvers(Entities(Solver.toEntities(), Solver.default().id)))

    private suspend fun resetMaze() = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)

        _store.value = newState

        client.send(ResetMaze(newState.maze.toDto()))

    }

    private suspend fun updateMazeProperties(properties: MazeProperties) = clearScope(scope) {

        var updatedState = Intent.UpdateMazeProperties(properties).reduce(_store.value)

        properties.initializer?.let {
            updatedState = Intent.ResetMaze.reduce(updatedState)

            val flow = generatorManager.generate(updatedState.maze, it)
            updatedState = Intent.UpdateMaze(flow.toList().last()).reduce(updatedState)
        }

        _store.value = updatedState

        client.send(UpdateMaze(updatedState.maze.toDto()))

    }

    private suspend fun generate(message: GeneratorAction.Generate) = clearScope(scope) {

        val newState = Intent.ResetMaze.reduce(_store.value)

        _store.value = newState

        val flow = generatorManager.generate(newState.maze, message.generatorId)

        generatorJob = flow
            .delay(::generatorDelay)
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
                .delay(::solverDelay)
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
