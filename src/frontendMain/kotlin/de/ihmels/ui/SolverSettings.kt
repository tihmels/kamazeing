package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.CMessageType.SolverAction
import de.ihmels.Entities
import de.ihmels.SolverState
import de.ihmels.StateService
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.StringPair
import io.kvision.form.check.RadioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.Select
import io.kvision.html.ButtonStyle
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.panel.hPanel
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.serialization.Serializable

@Serializable
data class SolverForm(val selectedSolver: String, val speed: String)

fun Container.solverSettings(solvers: Entities) {

    sidebarCard("Solver") {

        val form = getFormPanel(solvers)

        hPanel(justify = JustifyContent.STRETCH, spacing = 5, noWrappers = true) {

            button("Cancel", style = ButtonStyle.DANGER, className = "flex-one").onClick {

                AppService.Request.solverAction(SolverAction.Cancel)

            }.bind(StateService.mazeState) {
                disabled = it.solverState == SolverState.IDLE
            }

            button("Generate", className = "flex-one").onClick {

                val generatorId = form.getData().selectedSolver.toInt()
                AppService.Request.solverAction(SolverAction.Solve(generatorId))

            }.bind(StateService.mazeState) {
                disabled = it.solverState == SolverState.RUNNING || it.initialized == false
            }
        }

    }

}

private fun Div.getFormPanel(solvers: Entities) = formPanel<SolverForm> {

    val solverStringPairs = solvers.entities.map { StringPair(it.id.toString(), it.name) }.sortedBy { it.second }
    val default = solvers.default?.toString() ?: solverStringPairs.firstOrNull()?.first

    add(SolverForm::selectedSolver, Select(solverStringPairs, value = default))

    add(
        SolverForm::speed, RadioGroup(
            listOf("1" to "Slow", "2" to "Medium", "3" to "Fast"),
            inline = true,
            value = "1"
        ) {

            addCssClass("no-label")

            subscribe {
                if (!it.isNullOrBlank()) {
                    AppService.Request.updateSolverSpeed(it.toInt())
                }
            }
        }
    )

}