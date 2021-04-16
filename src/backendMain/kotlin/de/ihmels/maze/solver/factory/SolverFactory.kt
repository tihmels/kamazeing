package de.ihmels.maze.solver.factory

import de.ihmels.maze.solver.MazeSolver

interface SolverFactory {
    fun createSolver(solver: Solver): MazeSolver
}