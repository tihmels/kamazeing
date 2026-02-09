package de.ihmels.maze

enum class Direction(val dy: Int, val dx: Int) {

    NORTH(-1, 0) {
        override fun opposite() = SOUTH
    },
    EAST(0, 1) {
        override fun opposite() = WEST
    },
    SOUTH(1, 0) {
        override fun opposite() = NORTH
    },
    WEST(0, -1) {
        override fun opposite() = EAST
    };

    abstract fun opposite(): Direction

}