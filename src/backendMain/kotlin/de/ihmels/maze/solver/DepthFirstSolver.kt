package de.ihmels.maze.solver

import de.ihmels.maze.Maze
import de.ihmels.maze.Point2D
import de.ihmels.maze.graph.TreeNode
import java.util.*

class DepthFirstSolver : IMazeSolver {

    override fun solve(maze: Maze): TreeNode<Point2D>? {
        return dfs(maze.start, maze::isDestination, maze::successors)
    }

    private fun <T> dfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): TreeNode<T>? {

        val frontier: Stack<TreeNode<T>> = Stack()
        frontier.push(TreeNode(initial))

        val explored = mutableSetOf<T>()
        explored.add(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.pop()
            val currentState = currentNode.value

            if (exitPredicate(currentState)) {
                return currentNode
            }

            for (child in successors(currentState)) {

                if (explored.contains(child)) {
                    continue
                }

                explored.add(child)
                frontier.push(TreeNode(child, currentNode))
            }
        }

        return null
    }

}