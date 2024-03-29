package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.StateService
import io.kvision.form.check.checkBox
import io.kvision.form.formPanel
import io.kvision.form.spinner.Spinner
import io.kvision.html.ButtonType
import io.kvision.html.button
import io.kvision.modal.Modal
import kotlinx.serialization.Serializable

@Serializable
data class SettingsForm(val rows: Int? = null, val columns: Int? = null, val initialized: Boolean = false)

class SettingsModal : Modal(caption = "Maze Settings") {

    init {

        val rows = StateService.mazeState.getState().maze?.rows
        val columns = StateService.mazeState.getState().maze?.columns

        val formPanel = formPanel<SettingsForm> {

            add(
                SettingsForm::rows,
                Spinner(label = "Rows", min = 5, max = 15, value = rows)
            )

            add(
                SettingsForm::columns,
                Spinner(label = "Columns", min = 5, max = 15, value = columns)
            )

            add(
                SettingsForm::initialized,
                checkBox(
                    label = "Initialized (will use the currently selected generator)",
                    value = false,
                    rich = true
                )
            )
        }

        button("Submit", type = ButtonType.SUBMIT, className = "mt-2").onClick {
            val formData = formPanel.getData()

            val initializer =
                if (formData.initialized) StateService.generatorForm.getData().selectedGenerator.toInt() else null

            AppService.Request.updateMaze(
                rows = formData.rows,
                columns = formData.columns,
                initialized = initializer ?: -1
            )

            this@SettingsModal.hide()
        }

    }

}