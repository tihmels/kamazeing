package de.ihmels.maze.solver

class Node<T>(val state: T, val parent: Node<T>? = null)

fun <T> Node<T>.toList(): List<T> {

    var initial = this

    val path = mutableListOf<T>()
    path.add(initial.state)

    while (initial.parent != null) {
        initial = initial.parent!!
        path.add(0, initial.state)
    }

    return path
}