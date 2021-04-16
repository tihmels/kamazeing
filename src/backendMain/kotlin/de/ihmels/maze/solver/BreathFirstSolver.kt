package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.datastructure.ArrayListQueue
import de.ihmels.datastructure.Queue
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BreathFirstSolver : MazeSolver {

    override fun solve(maze: Maze): Flow<Node<Point2D>?> = bfs(maze.start, maze::isGoal, maze::successors)

    private fun <T> bfs(initial: T, exitPredicate: (T) -> Boolean, successors: (T) -> List<T>): Flow<Node<T>?> = flow {

        val frontier: Queue<Node<T>> = ArrayListQueue()
        frontier.enqueue(Node(initial, null))

        val visited = mutableSetOf<T>()
        visited.add(initial)

        while (frontier.isNotEmpty) {

            val currentNode = frontier.dequeue()
            require(currentNode != null)

            val currentState = currentNode.value

            emit(currentNode)

            if (exitPredicate(currentState!!)) {
                break;
            }

            for (child in successors(currentState)) {

                if (visited.contains(child)) {
                    continue
                }

                visited.add(child)
                frontier.enqueue(Node(child, currentNode))
            }
        }

        emit(null)
    }

}