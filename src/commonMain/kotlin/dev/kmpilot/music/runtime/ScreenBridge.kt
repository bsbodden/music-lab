package dev.kmpilot.music.runtime

// The editor bridge — publish the live logical state + structure to the host. Real on wasm; no-op on the jvm
// test harness. (expect/actual so the shared statecharts in commonMain can call it from tests too.)
expect fun publishScreenState(label: String, state: String)
expect fun publishChartSpec(json: String)
expect fun publishAppGraph(json: String)
expect fun publishCurrentScreen(name: String)
