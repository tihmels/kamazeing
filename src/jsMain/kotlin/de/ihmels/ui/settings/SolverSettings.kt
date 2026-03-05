package de.ihmels.ui.settings

import de.ihmels.AppService
import de.ihmels.RequestMessageType.SolverAction
import de.ihmels.AlgorithmOptions
import de.ihmels.FlowState
import de.ihmels.StateService
import de.ihmels.ui.solverStatusBadge
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.StringPair
import io.kvision.form.check.RadioGroup
import io.kvision.form.formPanel
import io.kvision.form.FormPanel
import io.kvision.form.select.TomSelect
import io.kvision.html.ButtonStyle
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.hPanel
import io.kvision.state.bind
import kotlinx.serialization.Serializable

@Serializable
data class SolverForm(val selectedSolver: String, val speed: String)

fun Container.solverSettings(solvers: AlgorithmOptions) {

    lateinit var form: FormPanel<SolverForm>

    div {
        form = getFormPanel(solvers)
    }

    hPanel(justify = JustifyContent.STRETCH, alignItems = AlignItems.CENTER, spacing = 5) {

        button("Cancel", style = ButtonStyle.DANGER, className = "flex-one") {
            onClick {
                AppService.Request.solverAction(SolverAction.Cancel)
            }
        }.bind(StateService.mazeState) {
            disabled = it.solverState == FlowState.IDLE
        }

        button("Skip", className = "flex-one") {
            onClick {
                AppService.Request.skipSolver()
            }
        }.bind(StateService.mazeState) {
            disabled = it.solverState == FlowState.IDLE
        }

        button("Solve", className = "flex-one") {
            onClick {
                val solverId = form.getData().selectedSolver.toInt()
                AppService.Request.solverAction(SolverAction.Solve(solverId))
            }
        }.bind(StateService.mazeState) {
            disabled = it.solverState == FlowState.RUNNING || it.initialized == false
        }

        // Status badge
        solverStatusBadge()
    }

}

private fun Div.getFormPanel(solvers: AlgorithmOptions) = formPanel<SolverForm> {

    val solverStringPairs = solvers.options.map { StringPair(it.id.toString(), it.name) }.sortedBy { it.second }
    val default = solvers.defaultId?.toString() ?: solverStringPairs.firstOrNull()?.first

    add(SolverForm::selectedSolver, TomSelect(solverStringPairs, value = default))

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
