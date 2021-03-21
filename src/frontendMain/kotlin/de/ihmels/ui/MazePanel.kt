package de.ihmels.ui

import de.ihmels.CellDto
import de.ihmels.MazeDto
import io.kvision.core.*
import io.kvision.core.Display.INLINEGRID
import io.kvision.html.div
import io.kvision.panel.gridPanel
import io.kvision.utils.px

const val cellSize = 50

val cellBorder = Border(1.px, BorderStyle.SOLID, Color.hex(0xA0A0A0))

fun Container.mazePanel(maze: MazeDto?) {

    maze?.let {

        gridPanel {

            display = INLINEGRID

            gridAutoRows = "1fr"
            gridTemplateColumns = "repeat(${it.columns}, ${cellSize}px)"

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
        height = cellSize.px

        if (cell.northEdge) borderTop = cellBorder
        if (cell.eastEdge) borderRight = cellBorder
        if (cell.southEdge) borderBottom = cellBorder
        if (cell.westEdge) borderLeft = cellBorder

        if (cell.isClosed()) {
            background = Background(Color.hex(0xEFEFEF))
        }
    }
}