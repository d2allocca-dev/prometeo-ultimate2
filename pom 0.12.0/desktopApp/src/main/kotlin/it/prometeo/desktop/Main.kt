package it.prometeo.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import it.prometeo.core.PrometeoApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "PROMETEO ULTIMATE") {
        PrometeoApp(platformName = "Windows / Desktop")
    }
}
