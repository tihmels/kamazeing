package de.ihmels.maze.generator.factory

import de.ihmels.maze.generator.MazeGenerator

interface GeneratorFactory {
    fun createGenerator(generator: Generator): MazeGenerator
}