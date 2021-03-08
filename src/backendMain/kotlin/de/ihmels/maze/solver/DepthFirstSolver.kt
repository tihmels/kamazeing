package de.ihmels.maze.solver

import de.ihmels.maze.Location
import de.ihmels.maze.Maze
import java.util.*

class DepthFirstSolver : IMazeSolver {

    override fun solve(maze: Maze): Node<Location>? {
        return dfs(maze.start, maze::isDestination, maze::successors)
    }

    private fun <T> dfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Node<T>? {

        val frontier: Stack<Node<T>> = Stack()
        frontier.push(Node(initial, null))

        val explored = mutableSetOf<T>()
        explored.add(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.pop()
            val currentState = currentNode.state

            if (exitPredicate(currentState)) {
                return currentNode
            }

            for (child in successors(currentState)) {

                if (explored.contains(child)) {
                    continue
                }

                explored.add(child)
                frontier.push(Node(child, currentNode))
            }
        }

        return null
    }

}