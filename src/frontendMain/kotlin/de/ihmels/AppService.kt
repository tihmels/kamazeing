package de.ihmels

object AppService {

    private val websocketHandler = WebsocketHandler {
        this.messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.NewMaze -> handleMessage(message)
    }

    private fun handleMessage(message: SMessageType.NewMaze) {
        StateService.updateMaze(message.maze)
    }

    fun connectToServer() = websocketHandler.connect()

}
