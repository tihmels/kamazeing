package de.ihmels.maze.solver.factory

import de.ihmels.IdAndName
import de.ihmels.maze.solver.*

enum class Solver(val id: Int, private val descriptor: String) {

    BREADTH_FIRST(0, "Breadth-First"),
    DEPTH_FIRST(1, "Depth-First"),
    ASTAR(2, "A-Star");

    companion object {
        fun toEntities() = entries.map { IdAndName(it.id, it.descriptor) }
        fun getSolverById(id: Int) = entries.find { it.id == id } ?: default()
        fun default() = BREADTH_FIRST

        fun createSolver(solver: Solver): MazeSolver = when (solver) {
            BREADTH_FIRST -> BreadthFirstSolver()
            DEPTH_FIRST -> DepthFirstSolver()
            ASTAR -> AstarSolver()
        }
    }

}
