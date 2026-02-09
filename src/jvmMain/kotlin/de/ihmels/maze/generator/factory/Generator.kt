package de.ihmels.maze.generator.factory

import de.ihmels.IdAndName
import de.ihmels.maze.generator.*

enum class Generator(val id: Int, private val descriptor: String) {

    ALDOUS_BRODER(0, "Aldous Broder"),
    BINARY_TREE(1, "Binary Tree"),
    DEPTH_FIRST(2, "Depth-First"),
    SIDEWINDER(3, "Sidewinder"),
    WILSON(4, "Wilson");

    companion object {
        fun toEntities() = entries.map { IdAndName(it.id, it.descriptor) }
        fun getGeneratorById(id: Int) = entries.find { it.id == id } ?: default()
        fun default() = ALDOUS_BRODER

        fun createGenerator(generator: Generator): MazeGenerator = when (generator) {
            ALDOUS_BRODER -> AldousBroderGenerator()
            BINARY_TREE -> BinaryTreeGenerator()
            DEPTH_FIRST -> DepthFirstGenerator()
            SIDEWINDER -> SidewinderGenerator()
            WILSON -> WilsonGenerator()
        }
    }

}
