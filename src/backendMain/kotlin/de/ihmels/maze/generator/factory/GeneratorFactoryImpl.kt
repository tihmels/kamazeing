package de.ihmels.maze.generator.factory

import de.ihmels.maze.generator.*

object GeneratorFactoryImpl : GeneratorFactory {

    override fun createGenerator(generator: Generator): MazeGenerator = when (generator) {
        Generator.ALDOUS_BRODER -> AldousBroderGenerator()
        Generator.BINARY_TREE -> BinaryTreeGenerator()
        Generator.DEPTH_FIRST -> DepthFirstGenerator()
        Generator.SIDEWINDER -> SidewinderGenerator()
        Generator.WILSON -> WilsonGenerator()
    }

}
