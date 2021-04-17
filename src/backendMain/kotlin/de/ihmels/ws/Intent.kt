package de.ihmels.ws

import de.ihmels.MazeProperties
import de.ihmels.maze.Maze
import de.ihmels.maze.bottomRight
import de.ihmels.maze.topLeft

sealed class Intent<T> {

    abstract fun reduce(old: T): T

    data class UpdateMaze(val m: Maze) : Intent<ClientState>() {
        override fun reduce(old: ClientState): ClientState =
            old.copy(maze = m, initialized = m.cells.none { it.isClosed() })
    }

    data class UpdateMazeProperties(val properties: MazeProperties) : Intent<ClientState>() {

        override fun reduce(old: ClientState): ClientState {

            val oldMaze = old.maze

            val newMaze = Maze(
                properties.rows ?: oldMaze.rows,
                properties.columns ?: oldMaze.columns,
                properties.start?.let { { _ -> it } } ?: topLeft,
                properties.goal?.let { { _ -> it } } ?: bottomRight)

            if (newMaze.dimensions == oldMaze.dimensions) {
                newMaze.grid = oldMaze.grid
            }

            return old.copy(maze = newMaze, initialized = newMaze.cells.none { it.isClosed() })

        }

    }

    object ResetMaze : Intent<ClientState>() {

        override fun reduce(old: ClientState): ClientState {

            val maze = old.maze.apply { reset() }

            return old.copy(maze = maze, initialized = false)

        }

    }

}