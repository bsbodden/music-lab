package dev.kmpilot.music.runtime

// Each js(...) must be a function's sole statement in Kotlin/Wasm.
actual fun publishScreenState(label: String, state: String) { js("globalThis.__screen = label + '=' + state") }
actual fun publishChartSpec(json: String) { js("globalThis.__chartSpec = json") }
actual fun publishAppGraph(json: String) { js("globalThis.__appGraph = json") }
actual fun publishCurrentScreen(name: String) { js("globalThis.__currentScreen = name") }
