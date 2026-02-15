package de.ihmels.ui

import de.ihmels.StateFlowService
import io.kvision.core.Container
import io.kvision.html.h4
import io.kvision.html.p

/**
 * Demonstration component showing modern KVision 9.x StateFlow usage patterns.
 *
 * This component shows how StateFlowService works alongside the existing Redux-based
 * StateService. Both services are kept in sync via AppService.messageHandler().
 *
 * **Pattern:**
 * StateFlowService provides a modern Kotlin coroutines-based alternative to Redux.
 * Both services receive the same updates for comparison and validation.
 *
 * **Usage for UI components:**
 * StateFlowService is ready to be used with KVision's bind() for reactive updates:
 *
 * ```kotlin
 * bind(StateFlowService.mazeState) { state ->
 *     // This UI code runs whenever state changes
 * }
 * ```
 */
fun Container.mazeStateFlowInfo() {
    h4("StateFlow Integration (KVision 9.x)")
    p("StateFlowService is now available for modern state management patterns.")
    p("Both StateService (Redux) and StateFlowService are kept in sync.")
    p("Ready to use for gradual migration to Kotlin coroutines patterns!")
}
