package de.ihmels.skippable

import de.ihmels.Point2D
import de.ihmels.SolverState
import de.ihmels.exception.FlowSkippedException
import de.ihmels.maze.Maze
import de.ihmels.maze.solver.Node
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.factory.SolverFactoryImpl
import kotlinx.coroutines.flow.*

class SolverStateFlow {

    private val _state = MutableStateFlow(SolverState.UNSOLVED)
    val state get() = _state.asStateFlow()

    fun solve(solverId: Int, maze: Maze): Flow<Node<Point2D>?> {

        val solver = getSolver(solverId)

        return solver.solve(maze)
            .onStart {
                _state.value = SolverState.SOLVING
            }
            .onCompletion { cause ->
                if (cause == null || cause is FlowSkippedException) {
                    _state.value = SolverState.SOLVED
                } else {
                    _state.value = SolverState.UNSOLVED
                }
            }

    }

    private fun getSolver(id: Int) =
        SolverFactoryImpl.createSolver(enumValues<Solver>().getOrElse(id) { Solver.default() })


}