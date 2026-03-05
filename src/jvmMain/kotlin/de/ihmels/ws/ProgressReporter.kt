package de.ihmels.ws

import de.ihmels.ProgressData
import de.ihmels.ResponseMessageType.UpdateProgress
import de.ihmels.StatisticsData
import de.ihmels.maze.Maze

object ProgressReporter {

    suspend fun reportProgress(
        maze: Maze,
        elapsedMs: Long,
        lastProgressUpdate: Long,
        client: Client
    ): Long {
        val visitedCells = maze.cells.count { !it.isClosed() }
        val totalCells = maze.size
        val percentComplete = (visitedCells.toDouble() / totalCells) * 100

        if (elapsedMs - lastProgressUpdate >= 200) {
            client.send(
                UpdateProgress(
                    ProgressData(
                        cellsProcessed = visitedCells,
                        totalCells = totalCells,
                        percentComplete = percentComplete,
                        elapsedMs = elapsedMs
                    )
                )
            )
            return elapsedMs
        }

        return lastProgressUpdate
    }

    fun calculateEfficiency(maze: Maze): Double {
        val visitedCells = maze.cells.count { !it.isClosed() }
        val totalCells = maze.size
        return (visitedCells.toDouble() / totalCells) * 100
    }

    fun createStatistics(
        algorithmName: String,
        durationMs: Long,
        maze: Maze,
        pathLength: Int = 0,
        algorithmType: String
    ): StatisticsData {
        val visitedCells = maze.cells.count { !it.isClosed() }
        val efficiency = calculateEfficiency(maze)

        return StatisticsData(
            algorithmName = algorithmName,
            durationMs = durationMs,
            cellsProcessed = visitedCells,
            pathLength = pathLength,
            efficiency = efficiency,
            algorithmType = algorithmType
        )
    }
}
