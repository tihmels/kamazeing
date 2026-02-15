package de.ihmels

import de.ihmels.CMessageType.*

object AppService {

    private val websocketHandler = WebsocketHandler {
        messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    private fun messageHandler(message: SMessageType) = when (message) {
        is SMessageType.UpdateMaze -> {
            StateService.updateMaze(message.maze)
            // Also update StateFlow for modern pattern testing
            StateFlowService.updateMaze(message.maze)
        }
        is SMessageType.UpdateGeneratorState -> {
            StateService.updateGeneratorState(message.state)
            StateFlowService.updateGeneratorState(message.state)
        }
        is SMessageType.UpdatePath -> {
            StateService.updatePath(message.path)
            StateFlowService.updatePath(message.path)
        }
        is SMessageType.ResetMaze -> {
            StateService.resetMaze(message.maze)
            StateFlowService.resetMaze(message.maze)
        }
        is SMessageType.Generators -> {
            StateService.updateGeneratorAlgorithms(message.generators)
            StateFlowService.updateGeneratorAlgorithms(message.generators)
        }
        is SMessageType.Solvers -> {
            StateService.updateSolverAlgorithms(message.solvers)
            StateFlowService.updateSolverAlgorithms(message.solvers)
        }
        is SMessageType.UpdateSolverState -> {
            StateService.updateSolverState(message.state)
            StateFlowService.updateSolverState(message.state)
        }
    }

    object Request {

        fun resetMaze() = websocketHandler.send(ResetMazeGrid)

        fun updateMaze(
            rows: Int? = null,
            columns: Int? = null,
            start: Point2D? = null,
            goal: Point2D? = null,
            initialized: Int = -1,
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
