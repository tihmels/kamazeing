package de.ihmels.maze

enum class Direction(val dx: Int, val dy: Int) {

    NORTH(0, -1) {
        override fun opposite() = SOUTH
    },
    EAST(1, 0) {
        override fun opposite() = WEST
    },
    SOUTH(0, 1) {
        override fun opposite() = NORTH
    },
    WEST(-1, 0) {
        override fun opposite() = EAST
    };

    abstract fun opposite(): Direction

}