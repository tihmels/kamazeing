package de.ihmels.ws

import de.ihmels.MazeProperties
import de.ihmels.Point2D
import de.ihmels.maze.Maze
import de.ihmels.maze.solver.Node

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
                properties.start?.let { { _ -> it } } ?: { oldMaze.start },
                properties.goal?.let { { _ -> it } } ?: { oldMaze.goal })

            if (newMaze.dimensions == oldMaze.dimensions) {
                newMaze.grid = oldMaze.grid
            }

            return old.copy(maze = newMaze, initialized = newMaze.cells.none { it.isClosed() })

        }

    }

    data class UpdatePath(val path: Node<Point2D>) : Intent<ClientState>() {
        override fun reduce(old: ClientState): ClientState {
            return old.copy(path = path)
        }
    }

    data class UpdateGeneratorSpeed(val speed: Int) : Intent<ClientState>() {
        override fun reduce(old: ClientState): ClientState {
            val delay = when (speed) {
                1 -> 350L
                2 -> 200L
                3 -> 100L
                else -> 100L
            }
            return old.copy(generatorDelay = delay)
        }

    }

    data class UpdateSolverSpeed(val speed: Int) : Intent<ClientState>() {
        override fun reduce(old: ClientState): ClientState {
            val delay = when (speed) {
                1 -> 300L
                2 -> 150L
                3 -> 50L
                else -> 350L
            }
            return old.copy(solverDelay = delay)
        }

    }

    object ResetMaze : Intent<ClientState>() {

        override fun reduce(old: ClientState): ClientState {

            val maze = old.maze.apply { reset() }

            return old.copy(maze = maze, initialized = false)

        }

    }

}