package de.ihmels.maze.generator

import de.ihmels.maze.Direction
import de.ihmels.maze.Maze
import de.ihmels.maze.moveTo
import kotlinx.coroutines.flow.flow

class DepthFirstGenerator : MazeGenerator {

    override fun generate(maze: Maze) = flow {

        var randomCell = maze.cells.random()

        val stack = ArrayDeque<de.ihmels.maze.Cell>()
        stack.addLast(randomCell)
        val visited = mutableSetOf(randomCell)

        while (stack.isNotEmpty()) {

            val randomNeighborCell = Direction.entries
                .map(randomCell::moveTo)
                .filter(maze::contains)
                .filter { it !in visited }
                .map(maze::getCell)
                .randomOrNull()

            if (randomNeighborCell != null) {
                randomCell.connect(randomNeighborCell)
                randomCell = randomNeighborCell

                emit(maze)

                stack.addLast(randomCell)
                visited.add(randomCell)
            } else {
                randomCell = stack.removeLast()
            }

        }

    }

}
