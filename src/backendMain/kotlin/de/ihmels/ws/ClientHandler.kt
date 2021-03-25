package de.ihmels.ws


import de.ihmels.*
import de.ihmels.SMessageType.UpdateGeneratorState
import de.ihmels.SMessageType.UpdateMaze
import de.ihmels.exception.FlowSkippedException
import de.ihmels.maze.Maze
import de.ihmels.maze.bottomRight
import de.ihmels.maze.topLeft
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ClientHandler(private val client: Client) : Logging {

    private val log = logger()

    private val clientState = ClientState()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var generatorJob: Job? = null

    private var finalValue: MazeDto? = null

    suspend fun start() {

        try {

            for (message in client.input) {

                log.info("Incoming Message from Client [$client]: ${message.messageType.javaClass.simpleName}")

                handle(message.messageType)
            }

        } finally {
            scope.cancel()
        }

    }

    private suspend fun handle(message: CMessageType) = when (message) {
        is CMessageType.ResetMaze -> handleMessage(message)
        is CMessageType.SetGeneratorState -> handleMessage(message)
        is CMessageType.UpdateMaze -> handleMessage(message)
        else -> {
        }
    }

    private suspend fun handleMessage(message: CMessageType.UpdateMaze) {

        scope.cancelChildrenIfActive()

        clientState.maze = Maze(
            message.rows ?: clientState.maze.rows,
            message.columns ?: clientState.maze.columns,
            message.start?.let { { _ -> it } } ?: topLeft,
            message.goal?.let { { _ -> it } } ?: bottomRight)

        client.send(UpdateMaze(clientState.maze.toDto()))
    }

    private suspend fun handleMessage(message: CMessageType.ResetMaze) {

        scope.cancelChildrenIfActive()

        clientState.maze.reset()

        client.send(UpdateMaze(clientState.maze.toDto()))
    }

    private suspend fun handleMessage(message: CMessageType.SetGeneratorState) = when (message.command) {
        GeneratorCommand.START -> startGeneration()
        GeneratorCommand.SKIP -> skipGeneration()
    }

    private fun startGeneration() {

        scope.cancelChildrenIfActive()

        clientState.maze.reset()

        val generatorFlow = clientState.generator.generate(clientState.maze)

        generatorJob = generatorFlow
            .onStart {
                client.send(UpdateGeneratorState(GeneratorState.RUNNING))
            }
            .map { it.toDto() }
            .onLastEmission {
                finalValue = it
                client.send(UpdateGeneratorState(GeneratorState.SKIPPABLE))
            }
            .buffer(1000)
            .delay(150)
            .onEach {
                client.send(UpdateMaze(it))
            }
            .onCompletion { cause ->
                finalValue = null
                if (cause == null || cause is FlowSkippedException) {
                    client.send(UpdateGeneratorState(GeneratorState.INITIALIZED))
                } else {
                    client.send(UpdateGeneratorState(GeneratorState.UNINITIALIZED))
                }
            }
            .launchIn(this.scope)
    }

    private suspend fun skipGeneration() {

        finalValue?.let {

            generatorJob?.cancelAndJoin(FlowSkippedException())
            client.send(UpdateMaze(it))

        }

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