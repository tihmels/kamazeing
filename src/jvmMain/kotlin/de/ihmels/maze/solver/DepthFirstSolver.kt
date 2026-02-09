package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DepthFirstSolver : MazeSolver {

    override fun solve(maze: Maze): Flow<Node<Point2D>?> = dfs(maze.start, maze::isGoal, maze::successors)

    private fun <T> dfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Flow<Node<T>?> = flow {

        val frontier = ArrayDeque<Node<T>>()
        frontier.addLast(Node(initial, null))

        val visited = mutableSetOf(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.removeLast()
            val currentState = currentNode.value

            emit(currentNode)

            if (exitPredicate(currentState)) {
                break
            }

            for (child in successors(currentState)) {
                if (child in visited) continue

                visited.add(child)
                frontier.addLast(Node(child, currentNode))
            }
        }
    }

}
