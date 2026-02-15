package de.ihmels.ui

import de.ihmels.StateFlowService
import de.ihmels.StatisticsData
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h4
import io.kvision.html.span
import io.kvision.state.bind
import kotlin.math.roundToInt

fun Container.statisticsPanel() {

    bind(StateFlowService.mazeState) { state ->
        val statistics = state.statistics

        if (statistics.isNotEmpty()) {

            div(className = "statistics-panel") {

                val latest = statistics.last()

                div(className = "stats-grid") {

                    statCard("Algorithm", latest.algorithmName)
                    statCard("Duration", formatTime(latest.durationMs))
                    statCard("Cells Visited", latest.cellsProcessed.toString())
                    statCard("Efficiency", "${latest.efficiency.roundToInt()}%")

                    if (latest.pathLength > 0) {
                        statCard("Path Length", latest.pathLength.toString())
                    }

                }

            }

        }

    }

}

private fun Container.statCard(label: String, value: String) {
    div(className = "stat-card") {
        div(className = "stat-card-label") { +label }
        div(className = "stat-card-value") { +value }
    }
}

private fun formatTime(ms: Long): String {
    return when {
        ms < 1000 -> "${ms}ms"
        ms < 60000 -> "${(ms / 1000.0).roundToInt()}s"
        else -> {
            val minutes = ms / 60000
            val seconds = (ms % 60000) / 1000
            "${minutes}m ${seconds}s"
        }
    }
}
