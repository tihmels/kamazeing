package de.ihmels

import kotlinx.serialization.Serializable

@Serializable
open class Point2D(val row: Int, val column: Int) {

    override fun toString(): String {
        return "Point[$row:$column]"
    }

    override fun equals(other: Any?): Boolean =
        if (other is Point2D) {
            row == other.row && column == other.column
        } else {
            false
        }

    override fun hashCode(): Int {
        var hash = 23
        hash = hash * 31 + row
        hash = hash * 31 + column
        return hash
    }

}

@Serializable
class CellDto(
    val row: Int,
    val column: Int,
    val northEdge: Boolean = true,
    val eastEdge: Boolean = true,
    val southEdge: Boolean = true,
    val westEdge: Boolean = true,
) {

    fun isClosed() = northEdge && eastEdge && southEdge && westEdge

    fun toPoint2D() = Point2D(row, column)

}

@Serializable
data class MazeDto(val rows: Int, val columns: Int, val start: Point2D, val goal: Point2D, val grid: List<CellDto>)

@Serializable
enum class SolverState {
    UNSOLVED, SOLVING, SKIPPABLE, SOLVED
}

@Serializable
enum class GeneratorState {
    UNINITIALIZED, RUNNING, SKIPPABLE, INITIALIZED
}

@Serializable
enum class GeneratorCommand {
    START, SKIP
}

@Serializable
enum class PathCommand {
    START, SKIP
}

@Serializable
data class CMessage(val messageType: CMessageType)

@Serializable
sealed class CMessageType {

    @Serializable
    data class SetMazeGenerator(val command: GeneratorCommand) : CMessageType()

    @Serializable
    data class SetMazeSolver(val command: PathCommand) : CMessageType()

    @Serializable
    data class UpdateMaze(
        val rows: Int? = null,
        val columns: Int? = null,
        val start: Point2D? = null,
        val goal: Point2D? = null
    ) : CMessageType()

    @Serializable
    object Reset : CMessageType()

}

@Serializable
data class SMessage(val messageType: SMessageType)

@Serializable
sealed class SMessageType {

    @Serializable
    data class UpdateGenerator(val state: GeneratorState) : SMessageType()

    @Serializable
    data class ResetMaze(val maze: MazeDto) : SMessageType()

    @Serializable
    data class UpdateMaze(val maze: MazeDto) : SMessageType()

    @Serializable
    data class UpdatePath(val path: List<Point2D>) : SMessageType()

}
