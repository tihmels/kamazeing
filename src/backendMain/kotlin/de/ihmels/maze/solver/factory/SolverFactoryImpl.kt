package de.ihmels.maze.solver.factory

import de.ihmels.maze.solver.AstarSolver
import de.ihmels.maze.solver.BreathFirstSolver
import de.ihmels.maze.solver.DepthFirstSolver
import de.ihmels.maze.solver.MazeSolver

object SolverFactoryImpl : SolverFactory {

    override fun createSolver(solver: Solver): MazeSolver = when (solver) {
        Solver.BREATH_FIRST -> BreathFirstSolver()
        Solver.DEPTH_FIRST -> DepthFirstSolver()
        Solver.ASTAR -> AstarSolver()
    }

}