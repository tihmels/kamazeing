package de.ihmels.ws

import de.ihmels.maze.Maze

data class ClientState(
    val maze: Maze = Maze(rows = 10, columns = 10),
    val initialized: Boolean = false
)