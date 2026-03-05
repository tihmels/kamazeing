package de.ihmels

import de.ihmels.ui.*
import de.ihmels.ui.panels.*
import de.ihmels.ui.settings.*
import de.ihmels.ui.common.*
import io.kvision.*
import io.kvision.core.*
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h1
import io.kvision.html.h5
import io.kvision.panel.ContainerType
import io.kvision.panel.flexPanel
import io.kvision.panel.root
import io.kvision.utils.px
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.state.insertNotNull
import io.kvision.utils.perc

class App : Application() {

    init {
        @Suppress("UNUSED_EXPRESSION")
        kvappCss
    }

    override fun start() {
        AppService.connectToServer()

        root("kvapp", containerType = ContainerType.FIXED) {
            bind(AppService.connectionState) { connectionState ->
                header(connectionState == ConnectionState.CONNECTED)
                when (connectionState) {
                    ConnectionState.CONNECTED -> appView()
                    ConnectionState.ESTABLISHING -> centeredStatusView("Establishing Connection ...")
                    ConnectionState.DISCONNECTED -> centeredStatusView("Disconnected")
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
                flexPanel(
                    justify = JustifyContent.FLEXEND,
                    alignItems = AlignItems.CENTER,
                    spacing = 7
                ) {
                    height = 100.perc

                    headerButton("Settings", "Change maze dimensions") {
                        SettingsModal().show()
                    }

                    headerButton("Reset", "Reset cells") {
                        AppService.Request.resetMaze()
                    }
                }
            }
        }
    }
}

private fun Container.headerButton(
    label: String,
    tooltip: String,
    action: () -> Unit
) {
    button(label).apply {
        title = tooltip
        enableTooltip()
    }.onClick { action() }
}

private fun Container.appView() {
    AppService.Request.resetMaze()
    AppService.Request.getGeneratorAlgorithms()
    AppService.Request.getSolverAlgorithms()

    flexPanel(
        FlexDirection.ROW,
        FlexWrap.WRAP,
        alignItems = AlignItems.FLEXSTART
    ) {
        add(
            div {
                insertNotNull(StateService.mazeState.sub { it.maze }) {
                    mazePanel(it)
                }
            },
            grow = 1,
            basis = 300.px
        )

        add(
            div {
                sidebar {
                    sidebarCard("Generator") {
                        div().bind(StateService.mazeState.sub { it.generatorAlgorithms }) {
                            generatorSettings(it)
                        }
                    }

                    sidebarCard("Solver") {
                        div().bind(StateService.mazeState.sub { it.solverAlgorithms }) {
                            solverSettings(it)
                        }
                    }

                    sidebarCard("Progress", collapsible = true) {
                        progressIndicatorPanel()
                    }

                    sidebarCard("Statistics", collapsible = true) {
                        statisticsPanel()
                    }

                    sidebarCard("Comparison", collapsible = true) {
                        algorithmComparisonPanel()
                    }
                }
            },
            basis = 480.px
        )
    }
}

private fun Container.centeredStatusView(title: String) {
    flexPanel(
        justify = JustifyContent.CENTER,
        alignContent = AlignContent.CENTER,
        alignItems = AlignItems.CENTER,
        direction = FlexDirection.COLUMN
    ) {
        h5(title)
    }
}

@JsModule("/kotlin/css/kvapp.css")
external val kvappCss: dynamic

fun main() {
    startApplication(
        ::App,
        js("import.meta.webpackHot").unsafeCast<Hot?>(),
        BootstrapModule,
        BootstrapCssModule,
        TomSelectModule,
        FontAwesomeModule,
        CoreModule
    )
}