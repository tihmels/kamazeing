package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.CMessageType.GeneratorAction
import de.ihmels.Entities
import de.ihmels.FlowState
import de.ihmels.StateFlowService
import de.ihmels.StateService
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.StringPair
import io.kvision.form.check.CheckBox
import io.kvision.form.check.RadioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.TomSelect
import io.kvision.html.ButtonStyle
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.hPanel
import io.kvision.form.FormPanel
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.serialization.Serializable

@Serializable
data class GeneratorForm(
    val selectedGenerator: String,
    val speed: String,
    val comparisonMode: Boolean = false,
    val sizePreset: String = "50"
)

fun Container.generatorSettings(generators: Entities) {

    div {
        StateService.generatorForm = getFormPanel(generators)
    }

    val generatorState = StateService.mazeState.sub { it.generatorState }

    hPanel(justify = JustifyContent.STRETCH, spacing = 5) {

        button("Cancel", style = ButtonStyle.DANGER, className = "flex-one") {
            onClick {
                AppService.Request.generatorAction(GeneratorAction.Cancel)
            }
        }.bind(generatorState) {
            disabled = it == FlowState.IDLE
        }

        button("Generate", className = "flex-one") {
            onClick {
                val generatorId = StateService.generatorForm.getData().selectedGenerator.toInt()
                AppService.Request.generatorAction(GeneratorAction.Generate(generatorId))
            }
        }.bind(generatorState) {
            disabled = it == FlowState.RUNNING
        }
    }

}

private fun Div.getFormPanel(generators: Entities) = formPanel<GeneratorForm> {

    val generatorStringPairs = generators.entities.map { StringPair(it.id.toString(), it.name) }.sortedBy { it.second }
    val default = generators.default?.toString() ?: generatorStringPairs.firstOrNull()?.first

    add(
        GeneratorForm::selectedGenerator,
        TomSelect(generatorStringPairs, value = default)
    )

    add(
        GeneratorForm::speed, RadioGroup(
            listOf("1" to "Slow", "2" to "Medium", "3" to "Fast"),
            inline = true,
            value = "1",
        ) {

            addCssClass("no-label")

            subscribe {
                if (!it.isNullOrBlank()) {
                    AppService.Request.updateGeneratorSpeed(it.toInt())
                }
            }
        }
    )

    add(
        GeneratorForm::comparisonMode, CheckBox(
            label = "Comparison Mode",
            value = false
        ) {

            subscribe {
                StateFlowService.setComparisonMode(it ?: false)
            }
        }
    )

    add(
        GeneratorForm::sizePreset, TomSelect(
            listOf(
                StringPair("10", "Tiny (10×10)"),
                StringPair("15", "Small (15×15)"),
                StringPair("20", "Medium (20×20)"),
                StringPair("30", "Large (30×30)")
            ),
            value = "15"
        ) {

            subscribe {
                if (!it.isNullOrBlank()) {
                    val size = it.toInt()
                    AppService.Request.updateMaze(rows = size, columns = size)
                }
            }
        }
    )

}