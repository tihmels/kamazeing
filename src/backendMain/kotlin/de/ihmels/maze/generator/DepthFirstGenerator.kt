package de.ihmels.maze.generator

import de.ihmels.ds.ArrayListQueue
import de.ihmels.maze.Direction
import de.ihmels.maze.Maze
import de.ihmels.maze.moveTo
import kotlinx.coroutines.flow.flow

class DepthFirstGenerator : MazeGenerator {

    override fun generate(maze: Maze) = flow {

        var randomCell = maze.cells.random()

        val queue = ArrayListQueue(randomCell)
        val visited = mutableListOf(randomCell)

        while (!queue.isEmpty) {

            val randomNeighborCell = Direction.values()
                .map(randomCell::moveTo)
                .filter(maze::contains)
                .filter { it !in visited }
                .map(maze::getCell)
                .randomOrNull()

            if (randomNeighborCell != null) {
                randomCell.connect(randomNeighborCell)
                randomCell = randomNeighborCell

                emit(maze)

                queue.enqueue(randomCell)
                visited.add(randomCell)

            } else {
                randomCell = queue.dequeue()!!
            }

        }

    }

}