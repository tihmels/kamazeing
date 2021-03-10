package de.ihmels.maze.graph

class TreeNode<T>(val value: T, val parent: TreeNode<T>? = null)

fun <T> TreeNode<T>.toList(): List<T> {

    var initial = this

    val path = mutableListOf<T>()
    path.add(initial.value)

    while (initial.parent != null) {
        initial = initial.parent!!
        path.add(0, initial.value)
    }

    return path
}