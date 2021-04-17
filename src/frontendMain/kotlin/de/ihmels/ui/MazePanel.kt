package de.ihmels.ui

import de.ihmels.*
import io.kvision.core.*
import io.kvision.core.Display.INLINEGRID
import io.kvision.html.*
import io.kvision.panel.gridPanel
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.px

const val cellSize = 50

const val START_IMG = "go.png"
const val GOAL_IMG = "flag.png"

val cellBorder = Border(1.px, BorderStyle.SOLID, Color.hex(0xA0A0A0))

fun Container.mazePanel(maze: MazeDto) {

    gridPanel {

        marginBottom = 15.px

        display = INLINEGRID

        gridAutoRows = "1fr"
        gridTemplateColumns = "repeat(${maze.columns}, ${cellSize}px)"

        border = cellBorder

        val pathStore = StateService.mazeState.sub { it.solutionPath }

        bind(pathStore) { path ->

            for (cell in maze.grid) {

                options(rowStart = cell.row + 1, columnStart = cell.column + 1) {

                    cell(cell) {

                        when {
                            cell.toPoint2D() == maze.start -> {
                                image(START_IMG, responsive = true, centered = true, classes = setOf("p-2")).setDragDropData("text/plain", "start")
                            }
                            cell.toPoint2D() == maze.goal -> {
                                image(GOAL_IMG, responsive = true, centered = true, classes = setOf("p-2")).setDragDropData("text/plain", "goal")
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
                AppService.Request.updateMaze(start = cell.toPoint2D(), goal = StateService.mazeState.getState().maze?.goal)
            } else if (data == "goal") {
                AppService.Request.updateMaze(goal = cell.toPoint2D(), start = StateService.mazeState.getState().maze?.start)
            }
        }
    }
}

fun Container.dot(color: Color): Span =
    span(className = "dot") {
        background = Background(color)
    }