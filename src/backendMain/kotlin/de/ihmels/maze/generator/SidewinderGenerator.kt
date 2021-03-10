package de.ihmels.maze.generator

import de.ihmels.maze.Cell
import de.ihmels.maze.Direction.EAST
import de.ihmels.maze.Direction.NORTH
import de.ihmels.maze.Maze
import kotlin.random.Random

class SidewinderGenerator : IMazeGenerator {

    override fun generate(maze: Maze) {

        for (row in maze.grid.shuffled()) {

            val run = mutableListOf<Cell>()

            for (cell in row) {

                run += cell

                val atEasternBoundary = cell moveTo EAST !in maze
                val atNorthernBoundary = cell moveTo NORTH !in maze

                val shouldCloseOut = atEasternBoundary || (!atNorthernBoundary && Random.nextInt(2) == 0)

                if (shouldCloseOut) {

                    val randomCell = run.random()
                    val northLocation = randomCell moveTo NORTH

                    if (northLocation in maze) {
                        val northCell = maze.getCell(northLocation)
                        randomCell.connect(northCell)
                        run.clear()
                    }

                } else {
                    val eastCell = maze.getCell(cell moveTo EAST)
                    cell.connect(eastCell)
                }

            }

        }

    }

}
