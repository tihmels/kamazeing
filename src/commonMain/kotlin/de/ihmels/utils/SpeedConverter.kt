package de.ihmels.utils

object SpeedConverter {

    fun msToSpeedLevel(ms: Int): Int = when {
        ms > 200 -> 1
        ms > 100 -> 2
        else -> 3
    }

    fun speedLevelToDelay(speed: Int): Long = when (speed) {
        1 -> 300L
        2 -> 200L
        3 -> 100L
        else -> 200L
    }
}
