package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.CMessageType.SolverAction
import de.ihmels.Entities
import de.ihmels.SolverState
import de.ihmels.StateService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.select.Select
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.state.bind
import kotlinx.serialization.Serializable

@Serializable
data class SolverForm(val selectedSolver: String)

fun Container.solverSettings(solvers: Entities) {

    sidebarCard("Solver") {

        val form = getFormPanel(solvers)

        button("Solve").onClick {

            val solver = form.getData().selectedSolver.toInt()
            AppService.sendSolverCommand(SolverAction.Solve(solver))

        }.bind(StateService.mazeState) {
            disabled = it.solverState == SolverState.RUNNING || it.initialized == false
        }

    }

}

private fun Div.getFormPanel(solvers: Entities) = formPanel<SolverForm> {

    val solverStringPairs = solvers.entities.map { StringPair(it.id.toString(), it.name) }
    val default = solvers.default?.toString() ?: solverStringPairs.firstOrNull()?.first

    add(SolverForm::selectedSolver, Select(solverStringPairs, value = default, label = "Select Solver"))

}