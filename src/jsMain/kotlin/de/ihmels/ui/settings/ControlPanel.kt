package de.ihmels.ui.settings

import de.ihmels.AppService
import de.ihmels.StateFlowService
import de.ihmels.utils.SpeedConverter
import de.ihmels.ui.common.sidebarCard
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.check.CheckBox
import io.kvision.form.select.TomSelect
import io.kvision.html.div
import io.kvision.html.label
import io.kvision.panel.vPanel

fun Container.controlPanel() {

    sidebarCard("Advanced Controls") {

        vPanel(spacing = 10) {

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
                            AppService.Request.updateGeneratorSpeed(SpeedConverter.msToSpeedLevel(speed))
                            AppService.Request.updateSolverSpeed(SpeedConverter.msToSpeedLevel(speed))
                        }
                    }

                }

            }

            div(className = "control-group") {

                label(className = "control-label") { +"Maze Size" }

                TomSelect(
                    listOf(
                        StringPair("10", "Tiny (10×10)"),
                        StringPair("15", "Small (15×15)"),
                        StringPair("20", "Medium (20×20)"),
                        StringPair("30", "Large (30×30)"),
                        StringPair("40", "Extra Large (40×40)")
                    ),
                    value = "15"
                ) {

                    subscribe { newValue ->
                        if (!newValue.isNullOrBlank()) {
                            val size = newValue.toInt()
                            AppService.Request.updateMaze(rows = size, columns = size)
                        }
                    }

                }

            }

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
