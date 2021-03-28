package de.ihmels.ws


import de.ihmels.*
import de.ihmels.SMessageType.UpdateGenerator
import de.ihmels.SMessageType.UpdateMaze
import de.ihmels.exception.FlowSkippedException
import de.ihmels.maze.Maze
import de.ihmels.maze.solver.toList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ClientHandler(private val client: Client) : Logging {

    private val log = logger()

    private val clientState = ClientState()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var generatorJob: Job? = null
    private var solverJob: Job? = null

    private var finalValue: MazeDto? = null

    suspend fun start() {

        try {

            for (message in client.input) {

                log.info("Message from Client [$client]: ${message.messageType.javaClass.simpleName}")

                handle(message.messageType)
            }

        } finally {
            scope.cancel()
        }

    }

    private suspend fun handle(message: CMessageType) = when (message) {
        is CMessageType.Reset -> handleMessage(message)
        is CMessageType.UpdateMaze -> handleMessage(message)
        is CMessageType.SetMazeGenerator -> handleMessage(message)
        is CMessageType.SetMazeSolver -> handleMessage(message)
        else -> {
        }
    }

    private suspend fun handleMessage(message: CMessageType.UpdateMaze) {

        scope.cancelChildrenIfActive()

        val maze = Maze(
            message.rows ?: clientState.maze.rows,
            message.columns ?: clientState.maze.columns,
            message.start?.let { { _ -> it } } ?: { clientState.maze.start },
            message.goal?.let { { _ -> it } } ?: { clientState.maze.goal })

        if (maze.dimension == clientState.maze.dimension) {
            maze.grid = clientState.maze.grid
        }

        clientState.maze = maze

        client.send(UpdateMaze(clientState.maze.toDto()))
    }

    private suspend fun handleMessage(message: CMessageType.Reset) {

        scope.cancelChildrenIfActive()

        clientState.maze.reset()

        client.send(SMessageType.ResetMaze(clientState.maze.toDto()))
    }

    private suspend fun handleMessage(message: CMessageType.SetMazeGenerator) = when (message.command) {
        GeneratorCommand.START -> startGenerator()
        GeneratorCommand.SKIP -> skipGenerator()
    }

    private fun startGenerator() {

        scope.cancelChildrenIfActive()

        clientState.maze.reset()

        val generatorFlow = clientState.generator.generate(clientState.maze)

        generatorJob = generatorFlow
            .onStart {
                client.send(UpdateGenerator(GeneratorState.RUNNING))
            }
            .map { it.toDto() }
            .onLastEmission {
                finalValue = it
                client.send(UpdateGenerator(GeneratorState.SKIPPABLE))
            }
            .buffer(1000)
            .delay(150)
            .onEach {
                client.send(UpdateMaze(it))
            }
            .onCompletion { cause ->
                finalValue = null
                if (cause == null || cause is FlowSkippedException) {
                    client.send(UpdateGenerator(GeneratorState.INITIALIZED))
                } else {
                    client.send(UpdateGenerator(GeneratorState.UNINITIALIZED))
                }
            }
            .launchIn(this.scope)
    }

    private suspend fun skipGenerator() {

        finalValue?.let {

            generatorJob?.cancelAndJoin(FlowSkippedException())
            client.send(UpdateMaze(it))

        }

    }

    private suspend fun handleMessage(message: CMessageType.SetMazeSolver) = when (message.command) {
        PathCommand.START -> startPath()
        PathCommand.SKIP -> skipPath()
    }

    private fun skipPath() {

    }

    private fun startPath() {

        scope.cancelChildrenIfActive()

        val solvingFlow = clientState.solver.solve(clientState.maze)

        solverJob = solvingFlow
            .filterNotNull()
            .delay(150)
            .onEach {
                println("TEST")
                client.send(SMessageType.UpdatePath(it.toList()))
            }
            .launchIn(this.scope)

    }


}

fun CoroutineScope.cancelChildrenIfActive() {
    if (isActive) coroutineContext.cancelChildren()
}

fun <T> Flow<T>.onLastEmission(block: suspend (T?) -> Unit) = flow {
    var final: T? = null
    collect {
        emit(it)
        final = it
    }
    block(final)
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