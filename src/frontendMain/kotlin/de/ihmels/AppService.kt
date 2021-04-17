package de.ihmels

import de.ihmels.CMessageType.*

object AppService {

    private val websocketHandler = WebsocketHandler {
        messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.UpdateMaze -> handleMessage(message)
        is SMessageType.UpdateGeneratorState -> handleMessage(message)
        is SMessageType.UpdatePath -> handleMessage(message)
        is SMessageType.ResetMaze -> handleMessage(message)
        is SMessageType.Generators -> handleMessage(message)
        is SMessageType.Solvers -> handleMessage(message)
        is SMessageType.UpdateSolverState -> handleMessage(message)
    }

    private fun handleMessage(message: SMessageType.UpdateSolverState) {
        StateService.updateSolverState(message.state)
    }

    private fun handleMessage(message: SMessageType.Solvers) {
        StateService.updateSolverAlgorithms(message.solvers)
    }

    private fun handleMessage(message: SMessageType.Generators) {
        StateService.updateGeneratorAlgorithms(message.generators)
    }

    private fun handleMessage(message: SMessageType.UpdateGeneratorState) {
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

    object Request {

        fun resetMaze() = websocketHandler.send(ResetMazeGrid)

        fun updateMaze(
            rows: Int? = null,
            columns: Int? = null,
            start: Point2D? = null,
            goal: Point2D? = null,
            initialized: Int? = null,
        ) = websocketHandler.send(UpdateMazeProperties(MazeProperties(rows, columns, start, goal, initialized)))

        fun generatorAction(action: GeneratorAction) =
            websocketHandler.send(action)

        fun solverAction(action: SolverAction) = websocketHandler.send(action)

        fun getGeneratorAlgorithms() = websocketHandler.send(GetGeneratorAlgorithms)

        fun getSolverAlgorithms() = websocketHandler.send(GetSolverAlgorithms)

        fun updateGeneratorSpeed(speed: Int) {
            websocketHandler.send(GeneratorAction.SetSpeed(speed))
        }

        fun updateSolverSpeed(speed: Int) {
            websocketHandler.send(SolverAction.SetSpeed(speed))

        }

    }


}
