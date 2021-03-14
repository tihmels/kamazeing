package de.ihmels.maze.generator

import de.ihmels.maze.Direction
import de.ihmels.maze.Maze

class AldousBroderGenerator : IMazeGenerator {

    override fun generate(maze: Maze) {

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

                    visitedCells.add(neighborCell)
                    unvisited--
                }

                cell = neighborCell

            }
        }

    }

}