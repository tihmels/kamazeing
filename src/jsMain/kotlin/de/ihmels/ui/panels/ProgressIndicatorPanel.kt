package de.ihmels.ui.panels

import de.ihmels.ProgressData
import de.ihmels.StateFlowService
import de.ihmels.utils.formatTime
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.state.bind
import kotlin.math.roundToInt

fun Container.progressIndicatorPanel() {

    bind(StateFlowService.mazeState) { state ->
        val progress = state.progressStats

        if (progress.totalCells > 0) {
            div(className = "progress-indicator-panel") {

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
