package de.ihmels.utils

import de.ihmels.AppService
import kotlinx.browser.document

/**
 * Keyboard shortcut definitions for Kamazeing
 *
 * Shortcuts:
 * - G: Generate maze
 * - S: Solve maze
 * - R: Reset maze
 * - Esc: Cancel operation
 * - D: Toggle dark mode (when implemented)
 * - ?: Show help
 */
object KeyboardShortcuts {

    /**
     * Initialize keyboard event listeners
     */
    fun init() {
        document.addEventListener("keydown", { event ->
            handleKeyPress(event as org.w3c.dom.events.KeyboardEvent)
        })
    }

    private fun handleKeyPress(event: org.w3c.dom.events.KeyboardEvent) {
        // Ignore if user is typing in an input field
        if (isInputFocused()) return

        when (event.key.lowercase()) {
            "g" -> {
                event.preventDefault()
                AppService.Request.getGeneratorAlgorithms()
                // Trigger generate (would need UI reference)
                console.log("Generate maze (G key pressed)")
            }
            "s" -> {
                event.preventDefault()
                // Trigger solve
                console.log("Solve maze (S key pressed)")
            }
            "r" -> {
                event.preventDefault()
                AppService.Request.resetMaze()
                console.log("Reset maze (R key pressed)")
            }
            "escape" -> {
                event.preventDefault()
                // Cancel current operation
                console.log("Cancel operation (Esc pressed)")
            }
            "d" -> {
                if (event.ctrlKey || event.metaKey) {
                    event.preventDefault()
                    toggleDarkMode()
                    console.log("Toggle dark mode (D key pressed)")
                }
            }
            "?" -> {
                event.preventDefault()
                showHelp()
                console.log("Show help (? key pressed)")
            }
        }
    }

    private fun isInputFocused(): Boolean {
        val activeElement = document.activeElement
        return activeElement?.let { element ->
            element.tagName.lowercase() in listOf("input", "textarea", "select")
        } ?: false
    }

    private fun toggleDarkMode() {
        val html = document.documentElement
        if (html != null) {
            val isDarkMode = html.classList.contains("dark-mode")

            if (isDarkMode) {
                html.classList.remove("dark-mode")
                localStorage["theme"] = "light"
            } else {
                html.classList.add("dark-mode")
                localStorage["theme"] = "dark"
            }
        }
    }

    private fun showHelp() {
        // TODO: Implement help modal
        console.log("""
            Keyboard Shortcuts:
            G - Generate maze
            S - Solve maze
            R - Reset maze
            Esc - Cancel operation
            Ctrl+D - Toggle dark mode
            ? - Show this help
        """)
    }
}

// Browser console logging helper
external object console {
    fun log(vararg args: Any?)
    fun error(vararg args: Any?)
    fun warn(vararg args: Any?)
}

// localStorage access
external object localStorage {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}
