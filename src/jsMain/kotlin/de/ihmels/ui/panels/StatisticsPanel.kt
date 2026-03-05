package de.ihmels.ui.panels

import de.ihmels.StateFlowService
import de.ihmels.StatisticsData
import de.ihmels.utils.formatTime
import io.kvision.core.Container
import io.kvision.html.div
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
