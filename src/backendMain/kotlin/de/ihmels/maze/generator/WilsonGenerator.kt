package de.ihmels.maze.generator

import de.ihmels.maze.Direction
import de.ihmels.maze.Maze
import de.ihmels.maze.moveTo
import kotlinx.coroutines.flow.flow

class WilsonGenerator : MazeGenerator {

    override fun generate(maze: Maze) = flow {

        val unvisited = maze.cells.toMutableList()

        unvisited.remove(unvisited.random())

        while (unvisited.isNotEmpty()) {

            var cell = unvisited.random()
            var path = mutableListOf(cell)

            while (cell in unvisited) { //randomWalk

                val neighborCell = Direction.values()
                    .map(cell::moveTo)
                    .filter(maze::contains)
                    .map(maze::getCell)
                    .random()

                val position = path.indexOf(neighborCell)

                if (position < 0) path.add(neighborCell) else path = path.subList(0, position + 1)

                cell = neighborCell
            }

            for (index in 0..path.size - 2) {
                path[index].connect(path[index + 1])
                unvisited.remove(path[index])

                emit(maze)
            }
        }

    }

}