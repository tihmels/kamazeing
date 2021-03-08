package de.ihmels.maze.generator

import de.ihmels.maze.Direction.EAST
import de.ihmels.maze.Direction.NORTH
import de.ihmels.maze.Maze

class BinaryTreeGenerator : IMazeGenerator {

    override fun generate(maze: Maze) {

        for (cell in maze.cells.shuffled()) {

            val neighborCell = listOf(NORTH, EAST)
                .map(cell::moveTo)
                .filter(maze::contains)
                .map(maze::get)
                .randomOrNull()

            neighborCell?.let {
                cell.connect(it)
            }
        }

    }

}