package de.ihmels

import io.kvision.Application
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication

class App : Application() {

    override fun start(state: Map<String, Any>) {

        val root = root("kvapp") {
        }

    }
}

fun main() {
    startApplication(::App, module.hot)
}
