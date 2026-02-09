package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BreadthFirstSolver : MazeSolver {

    override fun solve(maze: Maze): Flow<Node<Point2D>?> = bfs(maze.start, maze::isGoal, maze::successors)

    private fun <T> bfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Flow<Node<T>?> = flow {

        val frontier = ArrayDeque<Node<T>>()
        frontier.addLast(Node(initial, null))

        val visited = mutableSetOf(initial)

        while (frontier.isNotEmpty()) {

            val currentNode = frontier.removeFirst()
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

        emit(null)
    }

}
