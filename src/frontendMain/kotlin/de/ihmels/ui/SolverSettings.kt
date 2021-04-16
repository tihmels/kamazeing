package de.ihmels.ui

import de.ihmels.*
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.select.Select
import io.kvision.html.button
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.serialization.Serializable

@Serializable
data class SolverForm(val selectedSolver: String)

fun Container.solverSettings(solvers: Entities) {

    sidebarCard("Solver") {

        val form = formPanel<SolverForm> {

            val g = solvers.entities.map { StringPair(it.id.toString(), it.name) }
            add(SolverForm::selectedSolver, Select(g, value = "0", label = "Select Solver"))

        }

        button("Solve").onClick {

            val solver = form.getData().selectedSolver.toInt()
            AppService.sendSolverCommand(CMessageType.SolverAction.Solve(solver))

        }.bind(StateService.mazeState) {
            disabled = it.solverState == SolverState.RUNNING || it.initialized == false
        }

    }

}