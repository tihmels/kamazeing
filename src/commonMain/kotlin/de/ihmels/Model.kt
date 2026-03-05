package de.ihmels

import kotlinx.serialization.Serializable

@Serializable
open class Point2D(val row: Int, val column: Int) {

    fun isAbove(point: Point2D) = point.row == row + 1 && point.column == column

    fun isBelow(point: Point2D) = point.row == row - 1 && point.column == column

    fun isLeftTo(point: Point2D) = point.column == column + 1 && point.row == row

    fun isRightTo(point: Point2D) = point.column == column - 1 && point.row == row

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
data class AlgorithmOptions(val options: List<IdAndName> = emptyList(), val defaultId: Int? = null)

@Serializable
data class MazeDto(val rows: Int, val columns: Int, val start: Point2D, val goal: Point2D, val grid: List<CellDto>)

@Serializable
enum class FlowState {
    IDLE, RUNNING
}

@Serializable
data class MazeProperties(
    val rows: Int? = null,
    val columns: Int? = null,
    val start: Point2D? = null,
    val goal: Point2D? = null,
    val initializer: Int = -1
)

@Serializable
data class RequestMessage(val messageType: RequestMessageType)

@Serializable
sealed class RequestMessageType {

    @Serializable
    sealed class GeneratorAction : RequestMessageType() {

        @Serializable
        data class Generate(val generatorId: Int) : GeneratorAction()

        @Serializable
        data class CompareGenerators(val generator1Id: Int, val generator2Id: Int) : GeneratorAction()

        @Serializable
        data class SetSpeed(val speed: Int) : GeneratorAction()

        @Serializable
        object Cancel : GeneratorAction()

    }

    @Serializable
    sealed class SolverAction : RequestMessageType() {

        @Serializable
        data class Solve(val solverId: Int) : SolverAction()

        @Serializable
        data class SetSpeed(val speed: Int) : SolverAction()

        @Serializable
        object Cancel : SolverAction()

    }

    @Serializable
    object GetGeneratorAlgorithms : RequestMessageType()

    @Serializable
    object GetSolverAlgorithms : RequestMessageType()

    @Serializable
    data class UpdateMazeProperties(
        val properties: MazeProperties
    ) : RequestMessageType()

    @Serializable
    object ResetMazeGrid : RequestMessageType()

    @Serializable
    object SkipGenerator : RequestMessageType()

    @Serializable
    object SkipSolver : RequestMessageType()

}

@Serializable
data class ResponseMessage(val messageType: ResponseMessageType)

@Serializable
data class ProgressData(
    val cellsProcessed: Int = 0,
    val totalCells: Int = 0,
    val percentComplete: Double = 0.0,
    val elapsedMs: Long = 0L
)

@Serializable
data class StatisticsData(
    val algorithmName: String = "",
    val durationMs: Long = 0L,
    val cellsProcessed: Int = 0,
    val pathLength: Int = 0,
    val efficiency: Double = 0.0,
    val algorithmType: String = "" // "generator" or "solver"
)

@Serializable
data class ComparisonResult(
    val algorithm1: String = "",
    val algorithm2: String = "",
    val stats1: StatisticsData = StatisticsData(),
    val stats2: StatisticsData = StatisticsData(),
    val winner: String = "" // "" (tie) or algorithm name of winner
)

@Serializable
sealed class ResponseMessageType {

    @Serializable
    data class UpdateGeneratorState(val state: FlowState) : ResponseMessageType()

    @Serializable
    data class UpdateSolverState(val state: FlowState) : ResponseMessageType()

    @Serializable
    data class ResetMaze(val maze: MazeDto) : ResponseMessageType()

    @Serializable
    data class UpdateMaze(val maze: MazeDto) : ResponseMessageType()

    @Serializable
    data class Generators(val algorithms: AlgorithmOptions) : ResponseMessageType()

    @Serializable
    data class Solvers(val algorithms: AlgorithmOptions) : ResponseMessageType()

    @Serializable
    data class UpdatePath(val path: List<Point2D>) : ResponseMessageType()

    @Serializable
    data class UpdateProgress(val progress: ProgressData) : ResponseMessageType()

    @Serializable
    data class UpdateStatistics(val statistics: StatisticsData) : ResponseMessageType()

    @Serializable
    data class UpdateComparison(val result: ComparisonResult) : ResponseMessageType()

}
