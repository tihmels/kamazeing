package de.ihmels.maze.generator

import de.ihmels.maze.Direction.EAST
import de.ihmels.maze.Direction.NORTH
import de.ihmels.maze.Maze

/**
 * For each random cell, decide randomly whether to carve north or east.
 * Bias: northern row and eastern column are both unbroken corridors.
 * Paths are trivial: every cell has a corridor either north or east. So one can always move northeast without obstruction.
 */
class BinaryTreeGenerator : IMazeGenerator {

    override fun generate(maze: Maze) {

        for (cell in maze.cells.shuffled()) {

            val neighborCell = listOf(NORTH, EAST)
                .map(cell::moveTo)
                .filter(maze::contains)
                .map(maze::getCell)
                .randomOrNull()

            neighborCell?.let {
                cell.connect(it)
            }
        }

    }

}