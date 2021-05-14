package de.ihmels.skippable

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import de.ihmels.maze.solver.Node
import de.ihmels.maze.solver.factory.Solver
import de.ihmels.maze.solver.factory.SolverFactoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class SolverStateFlow(scope: CoroutineScope) : MazeStateFlowExecutor<Node<Point2D>?>(scope) {

    override fun getMazeFlowProvider(id: Int): (Maze) -> Flow<Node<Point2D>?> =
        SolverFactoryImpl.createSolver(Solver.getSolverById(id))::solve

}