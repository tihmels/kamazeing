package de.ihmels

object AppService {

    private val websocketHandler = WebsocketHandler {
        this.messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.UpdateMaze -> handleMessage(message)
        is SMessageType.UpdateGeneratorState -> handleMessage(message)
    }

    private fun handleMessage(message: SMessageType.UpdateGeneratorState) {
        StateService.updateGeneratorState(message.state)
    }

    private fun handleMessage(message: SMessageType.UpdateMaze) {
        StateService.updateMaze(message.maze)
    }

    fun updateMaze(
        rows: Int? = null,
        columns: Int? = null,
        start: Point2D? = null,
        goal: Point2D? = null
    ) = websocketHandler.send(CMessageType.UpdateMaze(rows, columns, start, goal))

    fun resetMaze() = websocketHandler.send(CMessageType.ResetMaze)

    fun sendGeneratorCommand(command: GeneratorCommand) = websocketHandler.send(CMessageType.SetGeneratorState(command))


}
