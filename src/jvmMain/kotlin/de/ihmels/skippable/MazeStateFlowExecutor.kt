package de.ihmels.skippable

import de.ihmels.FlowState
import de.ihmels.maze.Maze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class MazeStateFlowExecutor<T>(private val scope: CoroutineScope) {

    private val _state = MutableStateFlow(FlowState.IDLE)
    val state = _state.asStateFlow()

    private var flowJob: Job? = null
    private val skipRequested = AtomicBoolean(false)

    open fun execute(maze: Maze, id: Int, flowExtension: (Flow<T>).() -> Flow<*> = { this }) {

        val flowProvider = getMazeFlowProvider(id)
        skipRequested.set(false)

        flowJob = flow {
            var lastNonNullValue: T? = null
            flowProvider(maze).collect { value ->
                if (value != null) {
                    lastNonNullValue = value
                }
                if (!skipRequested.get()) {
                    emit(value)
                }
            }

            if (skipRequested.get() && lastNonNullValue != null) {
                @Suppress("UNCHECKED_CAST")
                emit(lastNonNullValue as T)
            }
        }
            .onStart {
                _state.value = FlowState.RUNNING
            }
            .onCompletion {
                _state.value = FlowState.IDLE
                flowJob = null
            }
            .flowExtension()
            .launchIn(scope)

    }

    fun getRawFlow(maze: Maze, id: Int): Flow<T> = getMazeFlowProvider(id)(maze)

    suspend fun cancel() {
        if (_state.value == FlowState.RUNNING) {
            flowJob?.cancelAndJoin()
        }
    }

    fun skip() {
        if (_state.value == FlowState.RUNNING) {
            skipRequested.set(true)
        }
    }

    abstract fun getMazeFlowProvider(id: Int): (Maze) -> Flow<T>

}