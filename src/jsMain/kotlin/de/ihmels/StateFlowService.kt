package de.ihmels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Modern StateFlow-based state management for Kamazeing.
 *
 * This is an alternative to the Redux-based StateService, demonstrating
 * KVision 9.x modern state management patterns using Kotlin coroutines Flow/StateFlow.
 *
 * **Why StateFlow?**
 * - Better alignment with backend Flow<Maze> patterns from Ktor
 * - More Kotlin-idiomatic (standard library approach)
 * - Excellent integration with KVision's bind() UI bindings
 * - Slightly better performance characteristics than custom observables
 *
 * **Usage Example:**
 * ```kotlin
 * bind(StateFlowService.mazeState.asObservable()) { state ->
 *     h1("Maze: ${state.maze?.columns}x${state.maze?.rows}")
 *     mazePanel(state.maze)
 * }
 * ```
 *
 * **Migration Note:**
 * This service works alongside StateService. You can gradually migrate components
 * from Redux-based StateService to StateFlow-based StateFlowService at your own pace.
 */
object StateFlowService {

    /**
     * Complete maze application state container.
     * Mirrors ClientState from StateService but uses immutable data patterns.
     */
    data class MazeFlowState(
        val maze: MazeDto? = null,
        val initialized: Boolean = false,
        val generatorState: FlowState = FlowState.IDLE,
        val solverState: FlowState = FlowState.IDLE,
        val solutionPath: List<Point2D> = emptyList(),
        val generatorAlgorithms: Entities = Entities(),
        val solverAlgorithms: Entities = Entities()
    )

    // Private mutable state
    private val _mazeState = MutableStateFlow(MazeFlowState())

    // Public read-only StateFlow for UI components
    /**
     * Observable maze state that automatically notifies subscribed UI components
     * when any state property changes.
     *
     * Use with KVision's bind() for reactive UI updates:
     * ```kotlin
     * bind(StateFlowService.mazeState.asObservable()) { state ->
     *     // This block re-executes whenever mazeState changes
     * }
     * ```
     */
    val mazeState: StateFlow<MazeFlowState> = _mazeState.asStateFlow()

    // State update functions - cleaner API than Redux dispatch

    fun updateMaze(maze: MazeDto) {
        _mazeState.value = _mazeState.value.copy(
            maze = maze,
            initialized = maze.grid.none { it.isClosed() },
            solutionPath = emptyList()
        )
    }

    fun updatePath(path: List<Point2D>) {
        _mazeState.value = _mazeState.value.copy(solutionPath = path)
    }

    fun updateGeneratorState(state: FlowState) {
        _mazeState.value = _mazeState.value.copy(generatorState = state)
    }

    fun updateSolverState(state: FlowState) {
        _mazeState.value = _mazeState.value.copy(solverState = state)
    }

    fun resetMaze(maze: MazeDto) {
        _mazeState.value = MazeFlowState(
            maze = maze,
            initialized = false,
            generatorState = FlowState.IDLE,
            solverState = FlowState.IDLE,
            solutionPath = emptyList()
        )
    }

    fun updateGeneratorAlgorithms(generators: Entities) {
        _mazeState.value = _mazeState.value.copy(generatorAlgorithms = generators)
    }

    fun updateSolverAlgorithms(solvers: Entities) {
        _mazeState.value = _mazeState.value.copy(solverAlgorithms = solvers)
    }

    fun reset() {
        _mazeState.value = MazeFlowState()
    }
}
