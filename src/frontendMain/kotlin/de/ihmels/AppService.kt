package de.ihmels

object AppService {

    private val websocketHandler = WebsocketHandler {
        this.messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.UpdateMaze -> handleMessage(message)
        is SMessageType.UpdateGenerator -> handleMessage(message)
        is SMessageType.UpdatePath -> handleMessage(message)
        is SMessageType.ResetMaze -> handleMessage(message)
    }

    private fun handleMessage(message: SMessageType.UpdateGenerator) {
        StateService.updateGeneratorState(message.state)
    }

    private fun handleMessage(message: SMessageType.UpdateMaze) {
        StateService.updateMaze(message.maze)
    }

    private fun handleMessage(message: SMessageType.UpdatePath) {
        StateService.updatePath(message.path)
    }

    private fun handleMessage(message: SMessageType.ResetMaze) {
        StateService.resetMaze(message.maze)
    }

    fun updateMaze(
        rows: Int? = null,
        columns: Int? = null,
        start: Point2D? = null,
        goal: Point2D? = null
    ) = websocketHandler.send(CMessageType.UpdateMaze(rows, columns, start, goal))

    fun resetMaze() = websocketHandler.send(CMessageType.Reset)

    fun sendGeneratorCommand(command: GeneratorCommand) = websocketHandler.send(CMessageType.SetMazeGenerator(command))
    fun sendPathCommand(command: PathCommand) = websocketHandler.send(CMessageType.SetMazeSolver(command))

}
