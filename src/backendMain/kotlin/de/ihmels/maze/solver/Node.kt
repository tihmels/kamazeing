package de.ihmels.maze.solver

data class Node<T>(val value: T, val parent: Node<T>?)

fun <T> Node<T>.toList(): List<T> {

    val list = mutableListOf(value)

    var parentNode = parent
    while (parentNode != null) {
        list.add(0, parentNode.value)
        parentNode = parentNode.parent
    }

    return list
}