package de.ihmels.datastructure

class ArrayListQueue<T>(vararg initialItems: T) : Queue<T> {

    private val list = arrayListOf<T>()

    override fun enqueue(item: T): Boolean {
        list.add(item)
        return true
    }

    override fun dequeue(): T? = if (isEmpty) null else list.removeAt(0)

    override val count: Int
        get() = list.size

    override fun peek(): T? = list.getOrNull(0)

    init {
        list.addAll(initialItems)
    }
}