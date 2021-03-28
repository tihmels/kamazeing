package de.ihmels

import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class MazeState(
    val maze: MazeDto? = null,
    val generatorState: GeneratorState = GeneratorState.UNINITIALIZED,
    val solverState: SolverState = SolverState.UNSOLVED,
    val path: List<Point2D> = emptyList()
)

object StateService {

    val mazeState = createReduxStore(::gridReducer, MazeState())

    sealed class StateAction : RAction {
        data class UpdateMaze(val maze: MazeDto) : StateAction()
        data class UpdatePath(val path: List<Point2D>) : StateAction()
        data class UpdateGenerator(val state: GeneratorState) : StateAction()
        data class UpdateSolver(val state: SolverState) : StateAction()
        data class ResetMaze(val maze: MazeDto) : StateAction()
    }

    private fun gridReducer(state: MazeState, action: StateAction): MazeState = when (action) {
        is StateAction.UpdateMaze -> state.copy(maze = action.maze)
        is StateAction.UpdateGenerator -> state.copy(generatorState = action.state)
        is StateAction.UpdateSolver -> state.copy(solverState = action.state)
        is StateAction.UpdatePath -> state.copy(path = action.path)
        is StateAction.ResetMaze -> state.copy(
            maze = action.maze,
            generatorState = GeneratorState.UNINITIALIZED,
            path = emptyList()
        )
    }

    fun updateMaze(maze: MazeDto) = mazeState.dispatch(StateAction.UpdateMaze(maze))
    fun updatePath(path: List<Point2D>) = mazeState.dispatch(StateAction.UpdatePath(path))
    fun updateGeneratorState(state: GeneratorState) = mazeState.dispatch(StateAction.UpdateGenerator(state))
    fun resetMaze(maze: MazeDto) = mazeState.dispatch(StateAction.ResetMaze(maze))

}