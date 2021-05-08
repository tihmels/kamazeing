package de.ihmels.skippable

import kotlinx.coroutines.flow.Flow

fun <T> Flow<T>.identity(): Flow<T> = this