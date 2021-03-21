package de.ihmels

object AppService {

    private val websocketHandler = WebsocketHandler {
        this.messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

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

    fun connectToServer() = websocketHandler.connect()

    fun getMaze() {
        websocketHandler.send(CMessageType.ResetMaze())
    }

    fun sendGeneratorCommand(command: GeneratorCommand) = websocketHandler.send(CMessageType.SetGeneratorState(command))

    fun generateMaze() {
        websocketHandler.send(CMessageType.SetGeneratorState(GeneratorCommand.START))
    }

    fun skipGeneration() {
        websocketHandler.send(CMessageType.SetGeneratorState(GeneratorCommand.SKIP))
    }

}
