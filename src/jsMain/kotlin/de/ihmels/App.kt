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
import io.kvision.core.FlexWrap
import io.kvision.core.FlexDirection
import io.kvision.core.AlignItems
import io.kvision.utils.px
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.perc

class App : Application() {

    init {
        kvappCss
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

    flexPanel(
        FlexDirection.ROW,
        FlexWrap.WRAP,
        alignItems = AlignItems.FLEXSTART
    ) {

        add(div {
            bind(mazeState) {
                if (it != null) {
                    val panel = mazePanel(it)
                }
            }
        }, grow = 1, basis = 300.px)

        add(div {
            val generatorStore = StateService.mazeState.sub { it.generatorAlgorithms }
            val solverStore = StateService.mazeState.sub { it.solverAlgorithms }

            sidebar {

                div().bind(generatorStore) {
                    generatorSettings(it)
                }

                div().bind(solverStore) {
                    solverSettings(it)
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

                controlPanel()

            }
        }, basis = 400.px)

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