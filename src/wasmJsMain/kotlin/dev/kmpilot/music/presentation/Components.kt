package dev.kmpilot.music.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import dev.kmpilot.components.media.warmImages
import dev.kmpilot.music.data.MusicRepository
import dev.kmpilot.music.domain.Collection
import dev.kmpilot.music.domain.Genre
import dev.kmpilot.music.domain.Track
import dev.kmpilot.music.runtime.ChartSpec
import dev.kmpilot.music.runtime.StateSpec
import dev.kmpilot.music.runtime.TransitionSpec
import dev.kmpilot.music.runtime.publishAppGraph
import dev.kmpilot.music.runtime.publishCurrentScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HomeComponent(ctx: ComponentContext, scope: CoroutineScope, repo: MusicRepository, val onOpen: (String) -> Unit) : ComponentContext by ctx {
    val shelves: List<Pair<String, List<Collection>>> = repo.home().map { it.title to it.collectionIds.mapNotNull(repo::collection) }
    val quickPicks: List<Collection> = repo.quickPicks().take(6)
    private val sm = ScreenMachine(scope, "Home")
    val phase: StateFlow<Phase> = sm.phase
    init { scope.launch { sm.start(); delay(200); sm.loaded(shelves.isEmpty()) } }
}

class SearchComponent(ctx: ComponentContext, private val scope: CoroutineScope, private val repo: MusicRepository, val onOpen: (String) -> Unit) : ComponentContext by ctx {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    val genres: List<Genre> = repo.genres()
    val results: StateFlow<List<Collection>> =
        _query.map { repo.search(it) }.stateIn(scope, SharingStarted.Eagerly, repo.search(""))
    private val sm = ScreenMachine(scope, "Search")
    val phase: StateFlow<Phase> = sm.phase
    init { scope.launch { sm.start(); sm.loaded(false) } }   // empty query → browse = Content
    fun setQuery(q: String) { _query.value = q; scope.launch { sm.loaded(repo.search(q).isEmpty()) } }
}

class LibraryComponent(ctx: ComponentContext, scope: CoroutineScope, repo: MusicRepository, val onOpen: (String) -> Unit) : ComponentContext by ctx {
    val items: List<Collection> = repo.library()
    private val sm = ScreenMachine(scope, "Library")
    val phase: StateFlow<Phase> = sm.phase
    init { scope.launch { sm.start(); delay(180); sm.loaded(items.isEmpty()) } }
}

class CollectionComponent(
    ctx: ComponentContext,
    scope: CoroutineScope,
    private val repo: MusicRepository,
    id: String,
    val player: Player,
    val onBack: () -> Unit,
) : ComponentContext by ctx {
    val collection: Collection? = repo.collection(id)
    val tracks: List<Track> = collection?.let(repo::tracks) ?: emptyList()
    val minutes: Int = collection?.let(repo::totalMinutes) ?: 0
    private val sm = ScreenMachine(scope, "Collection")
    val phase: StateFlow<Phase> = sm.phase
    init { scope.launch { sm.start(); delay(220); sm.loaded(tracks.isEmpty()) } }
    fun playFrom(index: Int) { if (tracks.isNotEmpty()) player.play(tracks, index, collection?.accent ?: 0xFF1DB954) }
    fun playAll() = playFrom(0)
}

class NowPlayingComponent(ctx: ComponentContext, val player: Player, val onBack: () -> Unit) : ComponentContext by ctx

class RootComponent(
    ctx: ComponentContext,
    private val scope: CoroutineScope,
    private val repo: MusicRepository,
) : ComponentContext by ctx {

    val player = Player(scope)
    private val nav = StackNavigation<Config>()
    val stack: Value<ChildStack<Config, Child>> = childStack(
        source = nav,
        serializer = Config.serializer(),
        initialConfiguration = Config.Home,
        handleBackButton = true,
        childFactory = ::child,
    )

    init {
        scope.launch { player.start() }
        warmImages(scope, repo.coverUrls())   // preload all covers so screens show real art, not gradients
        publishAppGraph(Json.encodeToString(APP_GRAPH))
        stack.subscribe { childStack -> publishCurrentScreen(childStack.active.configuration.screenName()) }
    }

    fun selectTab(tab: String) = nav.replaceAll(
        when (tab) { "Search" -> Config.Search; "Library" -> Config.Library; else -> Config.Home },
    )
    fun openNowPlaying() { if (stack.value.active.configuration !is Config.NowPlaying) nav.pushNew(Config.NowPlaying) }

    companion object {
        val APP_GRAPH = ChartSpec(
            id = "App", initial = "Home",
            states = listOf(
                StateSpec("Home", "home"), StateSpec("Search", "search"), StateSpec("Library", "list"),
                StateSpec("Collection", "detail"), StateSpec("NowPlaying", "detail"),
            ),
            transitions = listOf(
                TransitionSpec("Home", "Search", "Search tab"),
                TransitionSpec("Home", "Library", "Library tab"),
                TransitionSpec("Search", "Home", "Home tab"),
                TransitionSpec("Library", "Home", "Home tab"),
                TransitionSpec("Home", "Collection", "Open"),
                TransitionSpec("Search", "Collection", "Open"),
                TransitionSpec("Library", "Collection", "Open"),
                TransitionSpec("Collection", "NowPlaying", "Play"),
                TransitionSpec("Collection", "Home", "Back"),
                TransitionSpec("NowPlaying", "Collection", "Back"),
            ),
        )
    }

    private fun child(config: Config, childCtx: ComponentContext): Child = when (config) {
        Config.Home -> Child.Home(HomeComponent(childCtx, scope, repo, onOpen = { nav.pushNew(Config.Collection(it)) }))
        Config.Search -> Child.Search(SearchComponent(childCtx, scope, repo, onOpen = { nav.pushNew(Config.Collection(it)) }))
        Config.Library -> Child.Library(LibraryComponent(childCtx, scope, repo, onOpen = { nav.pushNew(Config.Collection(it)) }))
        is Config.Collection -> Child.Collection(CollectionComponent(childCtx, scope, repo, config.id, player, onBack = { nav.pop() }))
        Config.NowPlaying -> Child.NowPlaying(NowPlayingComponent(childCtx, player, onBack = { nav.pop() }))
    }

    fun navigateTo(screen: String) {
        when (screen) {
            "Home" -> nav.replaceAll(Config.Home)
            "Search" -> nav.replaceAll(Config.Search)
            "Library" -> nav.replaceAll(Config.Library)
            "Collection" -> nav.replaceAll(Config.Home, Config.Collection(repo.library().first().id))
            "NowPlaying" -> {
                if (player.state.value.track == null) repo.library().first().let { player.play(repo.tracks(it), 0, it.accent) }
                nav.replaceAll(Config.Home, Config.NowPlaying)
            }
        }
    }

    @Serializable
    sealed interface Config {
        @Serializable data object Home : Config
        @Serializable data object Search : Config
        @Serializable data object Library : Config
        @Serializable data class Collection(val id: String) : Config
        @Serializable data object NowPlaying : Config
    }

    sealed interface Child {
        class Home(val component: HomeComponent) : Child
        class Search(val component: SearchComponent) : Child
        class Library(val component: LibraryComponent) : Child
        class Collection(val component: CollectionComponent) : Child
        class NowPlaying(val component: NowPlayingComponent) : Child
    }

    private fun Config.screenName(): String = when (this) {
        Config.Home -> "Home"; Config.Search -> "Search"; Config.Library -> "Library"
        is Config.Collection -> "Collection"; Config.NowPlaying -> "NowPlaying"
    }
}
