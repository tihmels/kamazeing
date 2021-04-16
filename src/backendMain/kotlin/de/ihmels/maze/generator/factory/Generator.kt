package de.ihmels.maze.generator.factory

import de.ihmels.IdAndName

enum class Generator(private val id: Int, private val descriptor: String) {

    ALDOUS_BRODER(0, "Aldous Broder"),
    BINARY_TREE(1, "Binary Tree"),
    DEPTH_FIRST(2, "Depth-First"),
    SIDEWINDER(3, "Sidewinder"),
    WILSON(4, "Wilson");

    companion object {
        fun toEntities() = values().map { IdAndName(it.id, it.descriptor) }
        fun default() = ALDOUS_BRODER
    }

}
