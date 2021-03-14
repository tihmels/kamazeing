package de.ihmels

import kotlinx.serialization.Serializable

@Serializable
data class MazeDto(val rows: Int, val columns: Int, val grid: List<CellDto>)

@Serializable
data class CellDto(
    val row: Int,
    val column: Int
) {
    var northEdge = true
    var eastEdge = true
    var southEdge = true
    var westEdge = true
}

@Serializable
data class CMessage(val messageType: CMessageType)

@Serializable
sealed class CMessageType {

}

@Serializable
data class SMessage(val messageType: SMessageType)

@Serializable
sealed class SMessageType {

    @Serializable
    data class NewMaze(val maze: MazeDto) : SMessageType()
}
