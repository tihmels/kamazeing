package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.CMessageType
import de.ihmels.StateFlowService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.check.CheckBox
import io.kvision.form.select.TomSelect
import io.kvision.html.div
import io.kvision.html.label
import io.kvision.html.span
import io.kvision.panel.vPanel

fun Container.controlPanel() {

    sidebarCard("Advanced Controls") {

        vPanel(spacing = 10) {

            // Speed Control (as Radio Group since browser input range has poor UX)
            div(className = "control-group") {

                label(className = "control-label") { +"Animation Speed" }

                TomSelect(
                    listOf(
                        StringPair("100", "Very Fast (100ms)"),
                        StringPair("200", "Fast (200ms)"),
                        StringPair("500", "Medium (500ms)"),
                        StringPair("800", "Slow (800ms)"),
                        StringPair("1000", "Very Slow (1000ms)")
                    ),
                    value = "500"
                ) {

                    subscribe { newValue ->
                        if (!newValue.isNullOrBlank()) {
                            val speed = newValue.toInt()
                            StateFlowService.setSpeed(speed)
                            AppService.Request.updateGeneratorSpeed(speedToLegacy(speed))
                            AppService.Request.updateSolverSpeed(speedToLegacy(speed))
                        }
                    }

                }

            }

            // Size Presets
            div(className = "control-group") {

                label(className = "control-label") { +"Maze Size" }

                TomSelect(
                    listOf(
                        StringPair("20", "Small (20×20)"),
                        StringPair("35", "Medium (35×35)"),
                        StringPair("50", "Large (50×50)"),
                        StringPair("75", "Extra Large (75×75)"),
                        StringPair("100", "Huge (100×100)")
                    ),
                    value = "50"
                ) {

                    subscribe { newValue ->
                        if (!newValue.isNullOrBlank()) {
                            val size = newValue.toInt()
                            AppService.Request.updateMaze(rows = size, columns = size)
                        }
                    }

                }

            }

            // Step-through Mode
            div(className = "control-group") {

                CheckBox(
                    value = false
                ) {

                    label = "Step-Through Mode"

                    subscribe { checked ->
                        if (checked == true) {
                            StateFlowService.toggleStepThroughMode()
                        }
                    }

                }

            }

        }

    }

}

/**
 * Convert modern speed (milliseconds) to legacy speed (1-3 scale)
 * 1 = Slow (300ms), 2 = Medium (200ms), 3 = Fast (100ms)
 */
private fun speedToLegacy(ms: Int): Int = when {
    ms > 200 -> 1  // Slow
    ms > 100 -> 2  // Medium
    else -> 3      // Fast
}
