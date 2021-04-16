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
data class IdAndName(val id: Int, val name: String)

@Serializable
data class Entities(val entities: List<IdAndName> = emptyList(), val default: Int? = null)

@Serializable
data class MazeDto(val rows: Int, val columns: Int, val start: Point2D, val goal: Point2D, val grid: List<CellDto>)

@Serializable
enum class SolverState {
    IDLE, RUNNING
}

@Serializable
enum class GeneratorState {
    IDLE, RUNNING
}

@Serializable
data class MazeProperties(
    val rows: Int? = null,
    val columns: Int? = null,
    val start: Point2D? = null,
    val goal: Point2D? = null
)

@Serializable
data class CMessage(val messageType: CMessageType)

@Serializable
sealed class CMessageType {

    @Serializable
    sealed class GeneratorAction : CMessageType() {

        @Serializable
        data class Generate(val generatorId: Int) : GeneratorAction()

    }

    @Serializable
    sealed class SolverAction : CMessageType() {

        @Serializable
        data class Solve(val solverId: Int) : SolverAction()

    }

    @Serializable
    object GetGeneratorAlgorithms : CMessageType()

    @Serializable
    object GetSolverAlgorithms : CMessageType()

    @Serializable
    data class UpdateMazeProperties(
        val properties: MazeProperties
    ) : CMessageType()

    @Serializable
    object ResetMazeGrid : CMessageType()

}

@Serializable
data class SMessage(val messageType: SMessageType)

@Serializable
sealed class SMessageType {

    @Serializable
    data class UpdateGeneratorState(val state: GeneratorState) : SMessageType()

    @Serializable
    data class UpdateSolverState(val state: SolverState) : SMessageType()

    @Serializable
    data class ResetMaze(val maze: MazeDto) : SMessageType()

    @Serializable
    data class UpdateMaze(val maze: MazeDto) : SMessageType()

    @Serializable
    data class Generators(val generators: Entities) : SMessageType()

    @Serializable
    data class Solvers(val solvers: Entities) : SMessageType()

    @Serializable
    data class UpdatePath(val path: List<Point2D>) : SMessageType()

}