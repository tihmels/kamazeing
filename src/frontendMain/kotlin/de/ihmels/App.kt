package de.ihmels

import de.ihmels.ui.*
import io.kvision.*
import io.kvision.core.*
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h5
import io.kvision.panel.ContainerType
import io.kvision.panel.flexPanel
import io.kvision.panel.root
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.perc

class App : Application() {

    init {
        require("css/kvapp.css")
    }

    override fun start() {

        AppService.connectToServer()

        root("kvapp", containerType = ContainerType.FIXED) {

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

    div(className = "my-3 row border-bottom no-gutter") {

        div(className = "col") {
            h1("Kamazeing")
        }

        if (connected) {

            div(className = "col-auto") {

                flexPanel(justify = JustifyContent.FLEXEND, alignItems = AlignItems.CENTER, spacing = 7) {

                    height = 100.perc

                    button("Settings") {
                        title = "Change maze dimensions"
                        enableTooltip()
                    }.onClick {

                        SettingsModal().show()
                    }

                    button("Reset") {
                        title = "Reset cells"
                        enableTooltip()
                    }.onClick {
                        AppService.Request.resetMaze()
                    }
                }
            }
        }
    }
}

private fun Container.appView() {

    AppService.Request.resetMaze()

    AppService.Request.getGeneratorAlgorithms()
    AppService.Request.getSolverAlgorithms()

    val mazeState = StateService.mazeState.sub { it.maze }

    div(className = "row no-gutter") {

        div(className = "col").bind(mazeState) {
            if (it != null) {
                val panel = mazePanel(it)
            }
        }

        div(className = "col col-3") {

            val generatorStore = StateService.mazeState.sub { it.generatorAlgorithms }
            val solverStore = StateService.mazeState.sub { it.solverAlgorithms }

            sidebar {

                div().bind(generatorStore) {
                    generatorSettings(it)
                }

                div().bind(solverStore) {
                    solverSettings(it)
                }

            }

        }
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

fun main() {
    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, BootstrapSelectModule, FontAwesomeModule, CoreModule)
}