package de.ihmels.maze.solver.factory

import de.ihmels.IdAndName

enum class Solver(val id: Int, private val descriptor: String) {

    BREATH_FIRST(0, "Breath-First"),
    DEPTH_FIRST(1, "Depth-First"),
    ASTAR(2, "A-Star");

    companion object {
        fun toEntities() = values().map { IdAndName(it.id, it.descriptor) }
        fun default() = BREATH_FIRST
    }

}
