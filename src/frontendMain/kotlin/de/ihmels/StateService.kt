package de.ihmels

import de.ihmels.ui.GeneratorForm
import io.kvision.form.FormPanel
import io.kvision.redux.RAction
import io.kvision.redux.createReduxStore

data class ClientState(
    val maze: MazeDto? = null,
    val initialized: Boolean? = null,
    val generatorState: GeneratorState = GeneratorState.IDLE,
    val solverState: SolverState = SolverState.IDLE,
    val solutionPath: List<Point2D> = emptyList(),
    val generatorAlgorithms: Entities = Entities(),
    val solverAlgorithms: Entities = Entities()
)

object StateService {

    val mazeState = createReduxStore(::gridReducer, ClientState())

    var generatorForm: FormPanel<GeneratorForm> = FormPanel()

    sealed class StateAction : RAction {
        data class UpdateMaze(val maze: MazeDto) : StateAction()
        data class UpdatePath(val path: List<Point2D>) : StateAction()
        data class UpdateGeneratorState(val state: GeneratorState) : StateAction()
        data class UpdateGenerators(val generators: Entities) : StateAction()
        data class UpdateSolvers(val solvers: Entities) : StateAction()
        data class UpdateSolverState(val state: SolverState) : StateAction()
        data class ResetMaze(val maze: MazeDto) : StateAction()
    }

    private fun gridReducer(state: ClientState, action: StateAction): ClientState = when (action) {
        is StateAction.UpdateMaze -> state.copy(
            maze = action.maze,
            initialized = action.maze.grid.none { it.isClosed() },
            solutionPath = emptyList()
        )
        is StateAction.UpdateGeneratorState -> state.copy(generatorState = action.state)
        is StateAction.UpdateSolverState -> state.copy(solverState = action.state)
        is StateAction.UpdatePath -> state.copy(solutionPath = action.path)
        is StateAction.ResetMaze -> state.copy(
            maze = action.maze,
            initialized = false,
            generatorState = GeneratorState.IDLE,
            solverState = SolverState.IDLE,
            solutionPath = emptyList()
        )
        is StateAction.UpdateGenerators -> state.copy(generatorAlgorithms = action.generators)
        is StateAction.UpdateSolvers -> state.copy(solverAlgorithms = action.solvers)
    }

    fun updateMaze(maze: MazeDto) = mazeState.dispatch(StateAction.UpdateMaze(maze))
    fun updatePath(path: List<Point2D>) = mazeState.dispatch(StateAction.UpdatePath(path))
    fun updateGeneratorState(state: GeneratorState) = mazeState.dispatch(StateAction.UpdateGeneratorState(state))
    fun resetMaze(maze: MazeDto) = mazeState.dispatch(StateAction.ResetMaze(maze))
    fun updateSolverAlgorithms(solvers: Entities) = mazeState.dispatch(StateAction.UpdateSolvers(solvers))
    fun updateGeneratorAlgorithms(generators: Entities) = mazeState.dispatch(StateAction.UpdateGenerators(generators))
    fun updateSolverState(state: SolverState) = mazeState.dispatch(StateAction.UpdateSolverState(state))

}