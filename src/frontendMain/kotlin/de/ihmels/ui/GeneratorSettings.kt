package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.CMessageType.GeneratorAction
import de.ihmels.Entities
import de.ihmels.GeneratorState
import de.ihmels.StateService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.select.Select
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.serialization.Serializable

@Serializable
data class GeneratorForm(val selectedGenerator: String)

fun Container.generatorSettings(generators: Entities) {

    sidebarCard("Generator") {

        val form = getFormPanel(generators)

        val generatorState = StateService.mazeState.sub { it.generatorState }

        button("Generate").onClick {

            val generator = form.getData().selectedGenerator.toInt()
            AppService.Request.generatorAction(GeneratorAction.Generate(generator))

        }.bind(generatorState) {
            disabled = it == GeneratorState.RUNNING
        }

    }

}

private fun Div.getFormPanel(generators: Entities) = formPanel<GeneratorForm> {

    val generatorStringPairs = generators.entities.map { StringPair(it.id.toString(), it.name) }
    val default = generators.default?.toString() ?: generatorStringPairs.firstOrNull()?.first

    add(
        GeneratorForm::selectedGenerator,
        Select(generatorStringPairs, value = default, label = "Select Generator")
    )

}