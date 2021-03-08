package de.ihmels.maze

class GridIterator<T>(grid: List<List<T>>) : Iterator<T> {

    private val iterator: Iterator<T>

    init {
        val mutableList = mutableListOf<T>()

        for (row in grid.indices) {
            for (element in grid[row]) {
                mutableList.add(element)
            }
        }

        iterator = mutableList.iterator()
    }

    override fun hasNext() = iterator.hasNext()

    override fun next(): T = iterator.next()

}