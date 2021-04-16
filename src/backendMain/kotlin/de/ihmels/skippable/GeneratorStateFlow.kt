package de.ihmels.skippable

import de.ihmels.GeneratorState
import de.ihmels.exception.FlowSkippedException
import de.ihmels.maze.Maze
import de.ihmels.maze.generator.factory.Generator
import de.ihmels.maze.generator.factory.GeneratorFactoryImpl
import kotlinx.coroutines.flow.*

class GeneratorStateFlow {

    private val _state = MutableStateFlow(GeneratorState.UNINITIALIZED)
    val state = _state.asStateFlow()

    fun generateMaze(maze: Maze, generatorId: Int): Flow<Maze> {

        val generator = getGenerator(generatorId)

        return generator.generate(maze)
            .onStart {
                _state.value = GeneratorState.RUNNING
            }
            .onCompletion { cause ->
                if (cause == null || cause is FlowSkippedException) {
                    _state.value = GeneratorState.INITIALIZED
                } else {
                    _state.value = GeneratorState.UNINITIALIZED
                }
            }
    }

    fun skip() {

    }

    private fun getGenerator(id: Int) =
        GeneratorFactoryImpl.createGenerator(enumValues<Generator>().getOrElse(id) { Generator.default() })


}

fun <T> Flow<T>.skippable(skipUntil: (T) -> Boolean) = flow {
    collect { upstream ->
        try {
            emit(upstream)
        } catch (e: FlowSkippedException) {
            val v = dropWhile { skipUntil(upstream) }.first()
            emit(v)
        }

    }
}