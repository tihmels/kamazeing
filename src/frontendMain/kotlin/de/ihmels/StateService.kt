package de.ihmels

import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class MazeState(
    val maze: MazeDto? = null
)

object StateService {

    val mazeState = createReduxStore(::gridReducer, MazeState())

    sealed class StateAction : RAction {
        data class UpdateMaze(val maze: MazeDto) : StateAction()
    }

    private fun gridReducer(state: MazeState, action: StateAction): MazeState = when (action) {
        is StateAction.UpdateMaze -> state.copy(maze = action.maze)
    }

    fun updateMaze(maze: MazeDto) = mazeState.dispatch(StateAction.UpdateMaze(maze))

}