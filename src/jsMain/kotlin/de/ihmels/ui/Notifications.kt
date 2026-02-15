package de.ihmels.ui

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.utils.px
import io.kvision.core.Position
import io.kvision.core.Style

/**
 * Notification severity levels
 */
enum class NotificationLevel {
    SUCCESS, WARNING, ERROR, INFO
}

/**
 * Notification data class for displaying user feedback
 */
data class Notification(
    val message: String,
    val level: NotificationLevel = NotificationLevel.INFO,
    val duration: Int = 3000 // milliseconds
)

/**
 * Notification system for displaying toast notifications
 *
 * Usage:
 * ```kotlin
 * NotificationManager.show(
 *     Notification("Maze generated successfully!", NotificationLevel.SUCCESS)
 * )
 * NotificationManager.error("Failed to solve maze")
 * NotificationManager.info("Generating maze...")
 * ```
 */
object NotificationManager {
    private var notificationContainer: Container? = null

    fun init(container: Container) {
        notificationContainer = container.apply {
            addCssStyle(Style {
                position = Position.FIXED
                top = 20.px
                right = 20.px
                zIndex = 1000
            })
        }
    }

    fun show(notification: Notification) {
        notificationContainer?.apply {
            div(notification.message, className = "notification notification-${notification.level.name.lowercase()}") {
                addCssStyle(Style {
                    marginBottom = 10.px
                })
            }

            // Auto-remove after duration (simplified approach)
            kotlinx.browser.window.setTimeout({
                val notif = kotlinx.browser.document.querySelector(".notification")
                if (notif != null) {
                    @Suppress("UNCHECKED_CAST")
                    (notif as org.w3c.dom.HTMLElement).style.opacity = "0"
                    kotlinx.browser.window.setTimeout({
                        notif.remove()
                    }, 300)
                }
            }, notification.duration)
        }
    }

    fun success(message: String) = show(Notification(message, NotificationLevel.SUCCESS))
    fun error(message: String) = show(Notification(message, NotificationLevel.ERROR, 4000))
    fun warning(message: String) = show(Notification(message, NotificationLevel.WARNING))
    fun info(message: String) = show(Notification(message, NotificationLevel.INFO))
}
