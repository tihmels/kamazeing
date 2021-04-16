package de.ihmels.ui

import de.ihmels.*
import io.kvision.core.*
import io.kvision.core.Display.INLINEGRID
import io.kvision.html.Div
import io.kvision.html.Span
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.gridPanel
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.px

const val cellSize = 50

val cellBorder = Border(1.px, BorderStyle.SOLID, Color.hex(0xA0A0A0))

fun Container.mazePanel(maze: MazeDto?) {

    maze?.let {

        gridPanel {

            marginBottom = 15.px

            display = INLINEGRID

            gridAutoRows = "1fr"
            gridTemplateColumns = "repeat(${it.columns}, ${cellSize}px)"

            border = cellBorder

            val pathStore = StateService.mazeState.sub { it.solutionPath }

            bind(pathStore) { path ->

                for (cell in maze.grid) {

                    options(rowStart = cell.row + 1, columnStart = cell.column + 1) {

                        cell(cell) {

                            when {
                                cell.toPoint2D() == maze.start -> {
                                    dot(Color.name(Col.BLACK)).setDragDropData("text/plain", "start")
                                }
                                cell.toPoint2D() == maze.goal -> {
                                    dot(Color.name(Col.GREEN)).setDragDropData("text/plain", "goal")
                                }
                                cell.toPoint2D() in path -> {
                                    dot(Color.name(Col.GRAY))
                                }
                            }
                        }

                    }
                }

            }
        }
    }
}

fun Container.cell(cell: CellDto, init: Div.() -> Unit) {
    div {

        position = Position.RELATIVE

        height = cellSize.px
        width = cellSize.px

        if (cell.northEdge) borderTop = cellBorder
        if (cell.eastEdge) borderRight = cellBorder
        if (cell.southEdge) borderBottom = cellBorder
        if (cell.westEdge) borderLeft = cellBorder

        when {
            cell.isClosed() -> {
                background = Background(Color.hex(0xEFEFEF))
            }
            else -> {
                this.init()
            }
        }

        setDropTargetData("text/plain") { data ->
            if (data == "start") {
                AppService.Request.updateMaze(start = cell.toPoint2D())
            } else if (data == "goal") {
                AppService.Request.updateMaze(goal = cell.toPoint2D())
            }
        }
    }
}

fun Container.dot(color: Color): Span =
    span(className = "dot") {
        background = Background(color)
    }