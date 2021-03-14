package de.ihmels.maze.solver

import de.ihmels.maze.Maze
import de.ihmels.maze.Point2D
import java.util.*

class DepthFirstSolver : IMazeSolver {

    override fun solve(maze: Maze): Node<Point2D>? = dfs(maze.start, maze::isGoal, maze::successors)

    private fun <T> dfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Node<T>? {

        val frontier: Stack<Node<T>> = Stack()
        frontier.push(Node(initial, null))

        val visited = mutableSetOf<T>()
        visited.add(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.pop()
            val currentState = currentNode.value

            if (exitPredicate(currentState)) {
                return currentNode
            }

            for (child in successors(currentState)) {

                if (visited.contains(child)) {
                    continue
                }

                visited.add(child)
                frontier.push(Node(child, currentNode))
            }
        }

        return null
    }

}