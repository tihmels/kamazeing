package de.ihmels.ui

import de.ihmels.AppService
import de.ihmels.StateService
import io.kvision.form.formPanel
import io.kvision.form.spinner.Spinner
import io.kvision.html.ButtonType
import io.kvision.html.button
import io.kvision.modal.Modal
import kotlinx.serialization.Serializable

@Serializable
data class SettingsForm(val rows: Int? = null, val columns: Int? = null)

class SettingsModal : Modal(caption = "Maze Settings") {

    init {

        val rows = StateService.mazeState.getState().maze?.rows
        val columns = StateService.mazeState.getState().maze?.columns

        val formPanel = formPanel<SettingsForm> {
            add(
                SettingsForm::rows,
                Spinner(label = "Rows", min = 5, max = 17, value = rows)
            )
            add(
                SettingsForm::columns,
                Spinner(label = "Columns", min = 5, max = 17, value = columns)
            )
        }

        button("Submit", type = ButtonType.SUBMIT).onClick {
            val (row, column) = formPanel.getData()
            AppService.Request.updateMaze(rows = row, columns = column)
            this@SettingsModal.hide()
        }

    }

}