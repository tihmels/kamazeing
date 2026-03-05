package de.ihmels.ui

import de.ihmels.FlowState
import de.ihmels.StateService
import io.kvision.core.Container
import io.kvision.html.span
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.generatorStatusBadge() {
    span(className = "status-badge").bind(StateService.mazeState.sub { it.generatorState }) { state ->
        if (state == FlowState.RUNNING) {
            span(className = "status-badge-content running") { +"⏳ Running..." }
        } else {
            span(className = "status-badge-content idle") { +"✅ Ready" }
        }
    }
}

fun Container.solverStatusBadge() {
    span(className = "status-badge").bind(StateService.mazeState.sub { it.solverState }) { state ->
        if (state == FlowState.RUNNING) {
            span(className = "status-badge-content running") { +"⏳ Running..." }
        } else {
            span(className = "status-badge-content idle") { +"✅ Ready" }
        }
    }
}
