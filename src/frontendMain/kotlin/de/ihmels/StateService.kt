package de.ihmels

import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class MazeState(
    val maze: MazeDto? = null,
    val generatorState: GeneratorState = GeneratorState.UNINITIALIZED
)

object StateService {

    val mazeState = createReduxStore(::gridReducer, MazeState())

    sealed class StateAction : RAction {
        data class UpdateMaze(val maze: MazeDto) : StateAction()
        data class UpdateGeneratorState(val state: GeneratorState) : StateAction()
    }

    private fun gridReducer(state: MazeState, action: StateAction): MazeState = when (action) {
        is StateAction.UpdateMaze -> state.copy(maze = action.maze)
        is StateAction.UpdateGeneratorState -> state.copy(generatorState = action.state)
    }

    fun updateMaze(maze: MazeDto) = mazeState.dispatch(StateAction.UpdateMaze(maze))
    fun updateGeneratorState(state: GeneratorState) = mazeState.dispatch(StateAction.UpdateGeneratorState(state))

}