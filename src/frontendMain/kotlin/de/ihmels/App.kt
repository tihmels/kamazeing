package de.ihmels

import de.ihmels.ui.mazePanel
import io.kvision.Application
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h5
import io.kvision.module
import io.kvision.panel.flexPanel
import io.kvision.panel.root
import io.kvision.require
import io.kvision.startApplication
import io.kvision.state.bind
import io.kvision.state.sub

class App : Application() {

    init {
        require("css/kvapp.css")
    }

    override fun start() {

        AppService.connectToServer()

        root("kvapp") {
            div(className = "container") {

                bind(AppService.connectionState) {

                    header(it == ConnectionState.CONNECTED)

                    when (it) {
                        ConnectionState.CONNECTED -> appView()
                        ConnectionState.ESTABLISHING -> establishingView()
                        ConnectionState.DISCONNECTED -> disconnectedView()
                    }
                }
            }
        }
    }

    private fun Container.header(connected: Boolean) {
        div(classes = setOf("my-3", "border-bottom")) {
            h1("Kamazeing")
        }
    }

    private fun Container.appView() {

        val mazeStore = StateService.mazeState.sub { it.maze }

        div().bind(mazeStore) {
            mazePanel(it)
        }

    }

    private fun Container.establishingView() {

        flexPanel(
            justify = JustifyContent.CENTER,
            alignContent = AlignContent.CENTER,
            alignItems = AlignItems.CENTER,
            direction = FlexDirection.COLUMN
        ) {
            h5("Establishing Connection ...")
        }

    }

    private fun Container.disconnectedView() {
        flexPanel(
            justify = JustifyContent.CENTER,
            alignContent = AlignContent.CENTER,
            alignItems = AlignItems.CENTER,
            direction = FlexDirection.COLUMN
        ) {
            h5("Disconnected")
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
