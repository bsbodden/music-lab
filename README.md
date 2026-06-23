# Cadence (music-lab)

A complex, Spotify-style music app built from scratch to exercise the UI/nav/state patterns a code generator
must learn: a bottom-nav shell, carousels, a collapsing detail header, a persistent mini-player overlay, and a
real playback state machine. Stack: Compose Multiplatform + Decompose + KStateMachine.

This is a **TRUE multiplatform** KMPilot app:

- **Android + iOS** — the real targets. The shared Compose UI lives in `commonMain`.
- **wasm** — the in-browser editor preview (the catalog's live `preview`).
- **jvm** — a unit-test harness for the shared `commonMain` (transport machine, screen machine, repository).

## Build & test

```sh
export JAVA_HOME=$HOME/.sdkman/candidates/java/21.0.6-zulu
export ANDROID_HOME=$HOME/Android/Sdk
./gradlew jvmTest          # run the acceptance tests (proves commonMain compiles + logic passes)
./gradlew wasmJsBrowserRun # the in-browser preview
```

## iOS

iOS is built on a GitHub-hosted macOS runner via `.github/workflows/ios.yml` (manual trigger). It produces an
unsigned iOS Simulator `.app` and can stream it to Appetize.io.

## Known stubs (follow-ups)

- **Cover art** on Android/iOS/jvm renders the gradient placeholder only (`AsyncImage` `actual` calls the
  fallback). Real images via Coil3 are a follow-up; wasm decodes real covers in-browser.
- **Audio** on Android/iOS/jvm is a no-op (`AudioPlayer` `actual` does nothing). Real ExoPlayer/AVPlayer
  playback is a follow-up; wasm plays via `HTMLAudioElement`.
