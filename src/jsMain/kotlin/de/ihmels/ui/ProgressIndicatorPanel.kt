package de.ihmels.ui

import de.ihmels.ProgressData
import de.ihmels.StateFlowService
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h4
import io.kvision.html.span
import io.kvision.state.bind
import kotlin.math.roundToInt

fun Container.progressIndicatorPanel() {

    bind(StateFlowService.mazeState) { state ->
        val progress = state.progressStats

        if (progress.totalCells > 0) {
            div(className = "progress-indicator-panel") {

                h4("Progress")

                div(className = "progress-stats") {

                    div(className = "stat-row") {
                        span(className = "stat-label") { +"Cells Visited:" }
                        span(className = "stat-value") {
                            +"${progress.cellsProcessed}/${progress.totalCells}"
                        }
                    }

                    div(className = "stat-row") {
                        span(className = "stat-label") { +"Completion:" }
                        span(className = "stat-value") {
                            +"${progress.percentComplete.roundToInt()}%"
                        }
                    }

                    div(className = "stat-row") {
                        span(className = "stat-label") { +"Elapsed Time:" }
                        span(className = "stat-value") {
                            +formatTime(progress.elapsedMs)
                        }
                    }

                }

                div(className = "progress-bar-container") {
                    div(className = "progress-bar-fill") {
                        setAttribute("style", "width: ${progress.percentComplete.coerceIn(0.0, 100.0)}%")
                    }
                }

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
