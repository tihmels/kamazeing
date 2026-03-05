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
import org.w3c.dom.DragEvent
import org.w3c.dom.events.Event

const val cellSize = 50

const val START_IMG = "play-circle.svg"
const val GOAL_IMG = "target.svg"

const val STEP_UP = "step-up.png"
const val STEP_RIGHT = "step-right.png"
const val STEP_DOWN = "step-down.png"
const val STEP_LEFT = "step-left.png"

// Beveled wall colors (3D effect - light from top-left)
val wallBorderLight = Border(3.px, BorderStyle.SOLID, Color.hex(0x888888))
val wallBorderDark = Border(3.px, BorderStyle.SOLID, Color.hex(0x2a2a2a))
val mazeBorder = Border(2.px, BorderStyle.SOLID, Color.hex(0x404040))

fun Container.mazePanel(maze: MazeDto) {

    gridPanel {

        display = INLINEGRID

        gridAutoRows = "1fr"
        gridTemplateColumns = "repeat(${maze.columns}, ${cellSize}px)"

        border = mazeBorder

        // Create cells ONCE without bind - don't recreate on path updates
        for (cell in maze.grid) {

            options(rowStart = cell.row + 1, columnStart = cell.column + 1) {

                cell(cell) {

                    // Bind only the path content, not the entire cell
                    val pathStore = StateService.mazeState.sub { it.solutionPath }
                    bind(pathStore) { path ->
                        when (val point = cell.toPoint2D()) {
                            maze.start -> setStartCell()
                            maze.goal -> setGoalCell()
                            in path -> setPathCell(path, point)
                        }
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

        stepImage?.let { image(it, responsive = true, centered = true, className = "p-3") }

    } else {

        val predecessor = path.getOrElse(index - 1) { path[0] }

        val stepImage = when {
            point.isAbove(predecessor) -> STEP_UP
            point.isRightTo(predecessor) -> STEP_RIGHT
            point.isLeftTo(predecessor) -> STEP_LEFT
            point.isBelow(predecessor) -> STEP_DOWN
            else -> null
        }

        stepImage?.let { image(it, responsive = true, centered = true, className = "p-3") }

    }


}

private fun Div.setGoalCell() {
    image(
        GOAL_IMG,
        responsive = true,
        centered = true,
        className = "p-2"
    ){
        addCssStyle(Style {
            cursor = Cursor.POINTER
            color = Color.hex(0xEF4444) // Red target icon
        })
        draggable = true
    }.apply {
        setDragDropData("text/plain", "goal")
        getElement()?.addEventListener("dragstart", { e: Event ->
            val dragEvent = e as DragEvent
            dragEvent.dataTransfer?.effectAllowed = "move"
            dragEvent.dataTransfer?.setData("text/plain", "goal")
        })
    }
}

private fun Div.setStartCell() {
    image(
        START_IMG,
        responsive = true,
        centered = true,
        className = "p-2"
    ) {
        addCssStyle(Style {
            cursor = Cursor.POINTER
            color = Color.hex(0x22C55E) // Green play-circle icon
        })
        draggable = true
    }.apply {
        setDragDropData("text/plain", "start")
        getElement()?.addEventListener("dragstart", { e: Event ->
            val dragEvent = e as DragEvent
            dragEvent.dataTransfer?.effectAllowed = "move"
            dragEvent.dataTransfer?.setData("text/plain", "start")
        })
    }
}

fun Container.cell(cell: CellDto, init: Div.() -> Unit) {
    div(className = "maze-cell") {

        position = Position.RELATIVE

        height = cellSize.px
        width = cellSize.px

        // Apply wall styling
        if (cell.northEdge) borderTop = wallBorderLight
        if (cell.westEdge) borderLeft = wallBorderLight
        if (cell.southEdge) borderBottom = wallBorderDark
        if (cell.eastEdge) borderRight = wallBorderDark

        // Render the cell content
        when {
            cell.isClosed() -> {
                background = Background(Color.hex(0xEFEFEF))
            }
            else -> {
                this.init()
            }
        }

        // Set up drag and drop handlers - everything in one setEventListener
        setEventListener<Div> {
            dragenter = { e ->
                e.preventDefault()
                self.getElement()?.classList?.add("dragover-target")
                false
            }
            dragover = { e ->
                e.preventDefault()
                e.dataTransfer?.dropEffect = "move"
                self.getElement()?.classList?.add("dragover-target")
                false
            }
            dragleave = { e ->
                self.getElement()?.classList?.remove("dragover-target")
                false
            }
            drop = { e ->
                e.preventDefault()
                e.stopPropagation()
                self.getElement()?.classList?.remove("dragover-target")

                val data = e.dataTransfer?.getData("text/plain")
                val state = StateService.mazeState.getState()
                val cellPoint = cell.toPoint2D()
                if (data == "start" && state.maze?.start != cellPoint) {
                    AppService.Request.updateMaze(start = cellPoint, goal = state.maze?.goal)
                } else if (data == "goal" && state.maze?.goal != cellPoint) {
                    AppService.Request.updateMaze(goal = cellPoint, start = state.maze?.start)
                }
                false
            }
        }
    }
}