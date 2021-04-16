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
data class GeneratorForm(val selectedGenerator: String)

fun Container.generatorSettings(generators: Entities) {

    sidebarCard("Generator") {

        val form = formPanel<GeneratorForm> {

            val g = generators.entities.map { StringPair(it.id.toString(), it.name) }
            add(GeneratorForm::selectedGenerator, Select(g, value = "0", label = "Select Generator"))

        }

        val generatorState = StateService.mazeState.sub { it.generatorState }

        button("Generate").onClick {

            val generator = form.getData().selectedGenerator.toInt()
            AppService.sendGeneratorCommand(CMessageType.GeneratorAction.Generate(generator))

        }.bind(generatorState) {
            disabled = it == GeneratorState.RUNNING
        }

    }

}