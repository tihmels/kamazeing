package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class DepthFirstSolver : MazeSolver {

    override fun solve(maze: Maze): Flow<Node<Point2D>?> = dfs(maze.start, maze::isGoal, maze::successors)

    private fun <T> dfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Flow<Node<T>?> = flow {

        val frontier: Stack<Node<T>> = Stack()
        frontier.push(Node(initial, null))

        val visited = mutableSetOf<T>()
        visited.add(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.pop()
            val currentState = currentNode.value

            emit(currentNode)

            if (exitPredicate(currentState)) {
                emit(currentNode)
                break;
            }

            for (child in successors(currentState)) {

                if (visited.contains(child)) {
                    continue
                }

                visited.add(child)
                frontier.push(Node(child, currentNode))
            }
        }
    }

}