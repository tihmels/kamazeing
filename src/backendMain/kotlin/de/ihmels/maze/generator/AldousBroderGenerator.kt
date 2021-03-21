package de.ihmels.maze.generator

import de.ihmels.maze.Direction
import de.ihmels.maze.Maze
import de.ihmels.maze.moveTo
import kotlinx.coroutines.flow.flow

class AldousBroderGenerator : MazeGenerator {

    override fun generate(maze: Maze) = flow {

        var cell = maze.cells.random()

        val visitedCells = mutableListOf(cell)
        var unvisited = maze.size - 1

        while (unvisited > 0) {

            val randomDirection = Direction.values().random()

            val neighborLocation = cell moveTo randomDirection

            if (neighborLocation in maze) {

                val neighborCell = maze.getCell(neighborLocation)

                if (neighborCell !in visitedCells) {

                    cell.connect(neighborCell)
                    emit(maze)

                    visitedCells.add(neighborCell)
                    unvisited--
                }

                cell = neighborCell

            }
        }

    }

}