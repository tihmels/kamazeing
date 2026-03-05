package de.ihmels

import de.ihmels.RequestMessageType.*

object AppService {

    private val websocketHandler = WebsocketHandler {
        messageHandler(it.messageType)
    }

    val connectionState = websocketHandler.connectionState

    fun connectToServer() = websocketHandler.connect()

    private fun messageHandler(message: ResponseMessageType) = when (message) {
        is ResponseMessageType.UpdateMaze -> {
            StateService.updateMaze(message.maze)
            // Also update StateFlow for modern pattern testing
            StateFlowService.updateMaze(message.maze)
        }
        is ResponseMessageType.UpdateGeneratorState -> {
            StateService.updateGeneratorState(message.state)
            StateFlowService.updateGeneratorState(message.state)
        }
        is ResponseMessageType.UpdatePath -> {
            StateService.updatePath(message.path)
            StateFlowService.updatePath(message.path)
        }
        is ResponseMessageType.ResetMaze -> {
            StateService.resetMaze(message.maze)
            StateFlowService.resetMaze(message.maze)
        }
        is ResponseMessageType.Generators -> {
            StateService.updateGeneratorAlgorithms(message.algorithms)
            StateFlowService.updateGeneratorAlgorithms(message.algorithms)
        }
        is ResponseMessageType.Solvers -> {
            StateService.updateSolverAlgorithms(message.algorithms)
            StateFlowService.updateSolverAlgorithms(message.algorithms)
        }
        is ResponseMessageType.UpdateSolverState -> {
            StateService.updateSolverState(message.state)
            StateFlowService.updateSolverState(message.state)
        }
        is ResponseMessageType.UpdateProgress -> {
            StateFlowService.updateProgress(message.progress)
        }
        is ResponseMessageType.UpdateStatistics -> {
            StateFlowService.addStatistics(message.statistics)
        }
        is ResponseMessageType.UpdateComparison -> {
            StateFlowService.updateComparison(message.result)
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
        ) {
            // When dimensions change, reset start/goal to prevent out-of-bounds errors
            val resetStart = if (rows != null || columns != null) null else start
            val resetGoal = if (rows != null || columns != null) null else goal
            websocketHandler.send(UpdateMazeProperties(MazeProperties(rows, columns, resetStart, resetGoal, initialized)))
        }

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

        fun skipGenerator() = websocketHandler.send(RequestMessageType.SkipGenerator)

        fun skipSolver() = websocketHandler.send(RequestMessageType.SkipSolver)

    }

}
