package de.ihmels.ws

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect

interface SkippableFlow<out T> : Flow<T>

fun <T> Flow<T>.skippable(): Flow<T> =
    when (this) {
        is SkippableFlow<*> -> this
        else -> SkippableFlowImpl(this)
    }

public class SkippableFlowImpl<T>(private val flow: Flow<T>) : SkippableFlow<T> {

    var finalValue: T? = null

    override suspend fun collect(collector: FlowCollector<T>) {
        flow.collect {
            currentCoroutineContext().ensureActive()
            collector.emit(it)
            finalValue = it
        }
    }
}