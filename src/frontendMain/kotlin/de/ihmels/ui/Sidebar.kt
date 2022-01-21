package de.ihmels.ui

import io.kvision.core.*
import io.kvision.html.ButtonStyle
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.vPanel
import io.kvision.utils.px

fun Container.sidebar(block: Container.() -> Unit) {

    vPanel(spacing = 5) {
        block()
    }

}

fun Container.sidebarCard(title: String, collapsible: Boolean = false, init: Div.() -> Unit) {

    val allowedChars = ('A'..'Z') + ('a'..'z')
    val random = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    div(className = "card my-2") {

        div(className = "card-header") {
            content = title

            if (collapsible) {
                button(
                    text = "",
                    icon = "fas fa-caret-down",
                    style = ButtonStyle.OUTLINESECONDARY,
                    className = "btn-sm shadow-none"
                ) {

                    setAttribute("data-toggle", "collapse")
                    setAttribute("data-target", "#${random}")

                    border = Border(1.px, BorderStyle.HIDDEN, Color("black"))

                    float = PosFloat.RIGHT
                }
            }
        }
        div(className = "card-body test") {

            if (collapsible) {
                id = random
                addCssClass("collapse")
                addCssClass("show")
            }

            init.invoke(this)
        }
    }
}
