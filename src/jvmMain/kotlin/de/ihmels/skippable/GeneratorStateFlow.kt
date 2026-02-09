package de.ihmels.skippable

import de.ihmels.maze.Maze
import de.ihmels.maze.generator.factory.Generator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GeneratorStateFlow(scope: CoroutineScope) : MazeStateFlowExecutor<Maze>(scope) {

    override fun getMazeFlowProvider(id: Int): (Maze) -> Flow<Maze> =
        Generator.createGenerator(Generator.getGeneratorById(id))::generate

}
