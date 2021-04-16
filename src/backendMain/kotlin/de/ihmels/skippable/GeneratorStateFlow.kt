package de.ihmels.skippable

import de.ihmels.GeneratorState
import de.ihmels.maze.Maze
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.generator.factory.GeneratorFactoryImpl
import kotlinx.coroutines.flow.*

class GeneratorStateFlow {

    private val _state = MutableStateFlow(GeneratorState.IDLE)
    val state = _state.asStateFlow()

    fun generate(maze: Maze, generatorId: Int): Flow<Maze> {

        val generator = getGenerator(generatorId)

        return generator.generate(maze)
            .onStart {
                _state.value = GeneratorState.RUNNING
            }
            .onCompletion {
                _state.value = GeneratorState.IDLE
            }
    }

    private fun getGenerator(id: Int) =
        GeneratorFactoryImpl.createGenerator(enumValues<Generator>().getOrElse(id) { Generator.default() })

}