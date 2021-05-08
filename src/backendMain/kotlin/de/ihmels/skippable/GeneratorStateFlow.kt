package de.ihmels.skippable

import de.ihmels.maze.Maze
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.generator.factory.GeneratorFactoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GeneratorStateFlow(scope: CoroutineScope) : MazeFlowExecutor<Maze>(scope) {

    override fun getMazeFlowProvider(id: Int): (Maze) -> Flow<Maze> =
        GeneratorFactoryImpl.createGenerator(Generator.getGeneratorById(id))::generate

}