package de.ihmels.maze.generator

import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow

interface MazeGenerator {

    fun generate(maze: Maze): Flow<Maze>

}