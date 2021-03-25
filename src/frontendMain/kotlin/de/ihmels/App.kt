package de.ihmels

import de.ihmels.ui.mazePanel
import io.kvision.Application
import io.kvision.core.*
import io.kvision.html.button
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
import io.kvision.utils.perc

class App : Application() {

    init {
        require("css/kvapp.css")
    }

    override fun start() {

        AppService.connectToServer()

        root("kvapp", addRow = false) {

            div(classes = setOf("container")) {

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
}

private fun Container.header(connected: Boolean) {

    div(classes = setOf("my-3", "row", "border-bottom", "no-gutter")) {

        div(classes = setOf("col")) {
            h1("Kamazeing")
        }

        if (connected) {

            div(classes = setOf("col-auto")) {

                flexPanel(justify = JustifyContent.FLEXEND, alignItems = AlignItems.CENTER, spacing = 7) {

                    height = 100.perc

                    val generatorState = StateService.mazeState.sub { it.generatorState }

                    div().bind(generatorState) {

                        when (it) {
                            GeneratorState.UNINITIALIZED -> {
                                button("Generate") {
                                    onClick {
                                        AppService.sendGeneratorCommand(GeneratorCommand.START)
                                    }
                                }
                            }
                            GeneratorState.RUNNING -> {
                                button("Skip", disabled = true)
                            }
                            GeneratorState.SKIPPABLE -> {
                                button("Skip") {
                                    onClick {
                                        AppService.sendGeneratorCommand(GeneratorCommand.SKIP)
                                    }
                                }
                            }
                            GeneratorState.INITIALIZED -> {
                                button("Generate") {
                                    onClick {
                                        AppService.sendGeneratorCommand(GeneratorCommand.START)
                                    }
                                }
                            }
                        }

                    }

                    button("Reset") {
                        onClick {
                            AppService.resetMaze()
                        }
                    }
                }
            }
        }
    }
}

private fun Container.appView() {

    AppService.resetMaze()

    div().bind(StateService.mazeState) {
        mazePanel(it.maze)
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
    startApplication(::App, module.hot)
}
