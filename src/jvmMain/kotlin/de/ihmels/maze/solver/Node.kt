package de.ihmels.maze.solver

open class Node<T>(val value: T, val parent: Node<T>?)

class HeuristicNode<T>(value: T, parent: Node<T>?, val cost: Double, private val heuristic: Double) : Node<T>(value, parent),
    Comparable<HeuristicNode<T>> {

    override fun compareTo(other: HeuristicNode<T>): Int {
        val mine = cost + heuristic
        val theirs = other.cost + other.heuristic
        return mine.compareTo(theirs)
    }

}

fun <T> Node<T>.toList(): List<T> =
    generateSequence(this) { it.parent }
        .map { it.value }
        .toList()
        .reversed()