package de.ihmels.ds

interface Queue<T> {

    fun enqueue(item: T): Boolean
    fun dequeue(): T?

    val count: Int

    val isEmpty: Boolean
        get() = count == 0

    val isNotEmpty: Boolean
        get() = count != 0

    fun peek(): T?

}