package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class AstarSolver : MazeSolver {

    override fun solve(maze: Maze): Flow<Node<Point2D>?> =
        astar(maze.start, maze::isGoal, maze::successors, maze::manhattanDistance)

    private fun <T> astar(
        initial: T,
        exitPredicate: (T) -> Boolean,
        successors: (T) -> List<T>,
        heuristic: (T) -> Double
    ): Flow<Node<T>?> = flow {

        val frontier: PriorityQueue<HeuristicNode<T>> = PriorityQueue()
        frontier.offer(HeuristicNode(initial, null, 0.0, heuristic(initial)))

        val visited = mutableMapOf<T, Double>()
        visited[initial] = 0.0

        while (!frontier.isEmpty()) {

            val currentNode = frontier.poll()
            require(currentNode != null)

            emit(currentNode)
            val currentState = currentNode.value

            if (exitPredicate(currentState!!)) {
                break;
            }

            for (child in successors(currentState)) {

                val newCost = currentNode.cost + 1
                if (!visited.containsKey(child) || (visited[child] != null && visited[child]!! > newCost)) {
                    visited[child] = newCost
                    frontier.offer(HeuristicNode(child, currentNode, newCost, heuristic(child)))
                }
            }
        }

        emit(null)
    }

}