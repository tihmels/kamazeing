package de.ihmels.ui

import de.ihmels.*
import io.kvision.core.*
import io.kvision.core.Display.INLINEGRID
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.panel.gridPanel
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.px

const val cellSize = 50

const val START_IMG = "go.png"
const val GOAL_IMG = "flag.png"

const val STEP_UP = "step-up.png"
const val STEP_RIGHT = "step-right.png"
const val STEP_DOWN = "step-down.png"
const val STEP_LEFT = "step-left.png"

val cellBorder = Border(1.px, BorderStyle.SOLID, Color.hex(0x606060))

fun Container.mazePanel(maze: MazeDto) {

    gridPanel {

        display = INLINEGRID

        gridAutoRows = "1fr"
        gridTemplateColumns = "repeat(${maze.columns}, ${cellSize}px)"

        border = cellBorder

        val pathStore = StateService.mazeState.sub { it.solutionPath }

        bind(pathStore) { path ->

            for (cell in maze.grid) {

                options(rowStart = cell.row + 1, columnStart = cell.column + 1) {

                    cell(cell) {

                        when (val point = cell.toPoint2D()) {
                            maze.start -> setStartCell()
                            maze.goal -> setGoalCell()
                            in path -> setPathCell(path, point)
                        }

//                        contextMenu {
//                            cmLink("Start").onClick {
//                                AppService.Request.updateMaze(start = cell.toPoint2D())
//                            }
//                            cmLink("Goal").onClick {
//                                AppService.Request.updateMaze(goal = cell.toPoint2D())
//                            }
//                        }
                    }
                }
            }
        }
    }
}

private fun Div.setPathCell(
    path: List<Point2D>,
    point: Point2D
) {

    val index = path.indexOf(point)
    val successor = path.getOrNull(index + 1)

    if (successor != null) {

        val stepImage = when {
            successor.isAbove(point) -> STEP_UP
            successor.isRightTo(point) -> STEP_RIGHT
            successor.isLeftTo(point) -> STEP_LEFT
            successor.isBelow(point) -> STEP_DOWN
            else -> null
        }

        image(stepImage, responsive = true, centered = true, className = "p-3")

    } else {

        val predecessor = path.getOrElse(index - 1) { path[0] }

        val stepImage = when {
            point.isAbove(predecessor) -> STEP_UP
            point.isRightTo(predecessor) -> STEP_RIGHT
            point.isLeftTo(predecessor) -> STEP_LEFT
            point.isBelow(predecessor) -> STEP_DOWN
            else -> null
        }

        image(stepImage, responsive = true, centered = true, className = "p-3")

    }


}

private fun Div.setGoalCell() {
    image(
        GOAL_IMG,
        responsive = true,
        centered = true,
        className = "p-2"
    ){
        addCssStyle(Style { cursor = Cursor.POINTER })
    }.setDragDropData("text/plain", "goal")
}

private fun Div.setStartCell() {
    image(
        START_IMG,
        responsive = true,
        centered = true,
        className = "p-2"
    ) {
        addCssStyle(Style { cursor = Cursor.POINTER })
    }.setDragDropData("text/plain", "start")
}

fun Container.cell(cell: CellDto, init: Div.() -> Unit) {
    div(className = "maze-cell") {

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
            if (data == "start" && StateService.mazeState.getState().maze?.start != cell.toPoint2D()) {
                AppService.Request.updateMaze(
                    start = cell.toPoint2D(),
                    goal = StateService.mazeState.getState().maze?.goal
                )
            } else if (data == "goal" && StateService.mazeState.getState().maze?.goal != cell.toPoint2D()) {
                AppService.Request.updateMaze(
                    goal = cell.toPoint2D(),
                    start = StateService.mazeState.getState().maze?.start
                )
            }
        }
    }
}