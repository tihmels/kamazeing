package de.ihmels.ui

import de.ihmels.ComparisonResult
import de.ihmels.StateFlowService
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h4
import io.kvision.html.span
import io.kvision.state.bind
import kotlin.math.roundToInt

fun Container.algorithmComparisonPanel() {

    bind(StateFlowService.mazeState) { state ->
        val comparison = state.comparisonResult

        if (comparison != null) {
            div(className = "comparison-panel") {

                h4("Algorithm Comparison")

                div(className = "comparison-container") {

                    div(className = "comparison-column") {
                        div(className = "comparison-header") {
                            +comparison.algorithm1
                        }
                        comparisonStats(comparison.stats1, comparison.winner == comparison.algorithm1)
                    }

                    div(className = "comparison-column") {
                        div(className = "comparison-header") {
                            +comparison.algorithm2
                        }
                        comparisonStats(comparison.stats2, comparison.winner == comparison.algorithm2)
                    }

                }

                if (comparison.winner.isNotEmpty()) {
                    div(className = "comparison-winner") {
                        span(className = "winner-badge") {
                            +"🏆 Winner: ${comparison.winner}"
                        }
                    }
                }

            }

        }

    }

}

private fun Container.comparisonStats(stats: de.ihmels.StatisticsData, isWinner: Boolean) {

    div(className = "comparison-stats ${if (isWinner) "winner" else ""}") {

        div(className = "stat-row") {
            span(className = "stat-label") { +"Time:" }
            span(className = "stat-value") { +formatTime(stats.durationMs) }
        }

        div(className = "stat-row") {
            span(className = "stat-label") { +"Cells:" }
            span(className = "stat-value") { +"${stats.cellsProcessed}" }
        }

        div(className = "stat-row") {
            span(className = "stat-label") { +"Efficiency:" }
            span(className = "stat-value") { +"${stats.efficiency.roundToInt()}%" }
        }

        if (stats.pathLength > 0) {
            div(className = "stat-row") {
                span(className = "stat-label") { +"Path:" }
                span(className = "stat-value") { +"${stats.pathLength}" }
            }
        }

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
