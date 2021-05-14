package de.ihmels.skippable

import de.ihmels.FlowState
import de.ihmels.maze.Maze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*

abstract class MazeStateFlowExecutor<T>(private val scope: CoroutineScope) {

    private val _state = MutableStateFlow(FlowState.IDLE)
    val state = _state.asStateFlow()

    private var flowJob: Job? = null

    open fun execute(maze: Maze, id: Int, flowExtension: (Flow<T>).() -> Flow<*> = { identity() }) {

        val flowProvider = getMazeFlowProvider(id)

        flowJob = flowProvider(maze)
            .onStart {
                _state.value = FlowState.RUNNING
            }.onCompletion {
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

    abstract fun getMazeFlowProvider(id: Int): (Maze) -> Flow<T>

}