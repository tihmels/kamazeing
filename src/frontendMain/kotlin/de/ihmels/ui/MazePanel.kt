package de.ihmels.ui

import de.ihmels.CellDto
import de.ihmels.MazeDto
import io.kvision.core.Border
import io.kvision.core.BorderStyle
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.core.Display.INLINEGRID
import io.kvision.html.div
import io.kvision.panel.gridPanel
import io.kvision.utils.px

const val GRID_CELL_SIZE = "minmax(25px, 35px)"

val cellBorder = Border(1.px, BorderStyle.SOLID, Color.hex(0xA0A0A0))

fun Container.mazePanel(maze: MazeDto?) {

    maze?.let {

        gridPanel {

            display = INLINEGRID

            gridAutoRows = "1fr"
            gridTemplateColumns = "repeat(${it.columns}, $GRID_CELL_SIZE)"

            border = cellBorder

            for (cell in maze.grid) {

                options(rowStart = cell.row + 1, columnStart = cell.column + 1) {
                    cell(cell)
                }

            }
        }

    }
}

fun Container.cell(cell: CellDto) {
    div {
        height = 35.px

        if (cell.northEdge) borderTop = cellBorder
        if (cell.eastEdge) borderRight = cellBorder
        if (cell.southEdge) borderBottom = cellBorder
        if (cell.westEdge) borderLeft = cellBorder
    }
}