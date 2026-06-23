package dev.kmpilot.music.data

import dev.kmpilot.music.domain.Collection
import dev.kmpilot.music.domain.CollectionKind
import dev.kmpilot.music.domain.Genre
import dev.kmpilot.music.domain.Section
import dev.kmpilot.music.domain.Track

// REAL albums from the Mahorka netlabel (mahorka.org), Internet Archive, Creative Commons. Each album has its
// own real cover art + tracks; audio + covers stream from archive.org (CORS-open, range-supported).
// Attribution: the artists below + Mahorka. Not for commercial use.

internal val TRACKS: List<Track> = listOf(
    // yourai — my heart is so loud [mhrk063]
    Track("t1", "My Heart Is So Loud", "yourai", 132, "https://archive.org/download/mhrk063/mhrk063_yourai_01_my_heart_is_so_loud__original_single_mix.mp3", "https://archive.org/download/mhrk063/mhrk063.jpg"),
    Track("t2", "My Heart Is So Loud (Adrián Juárez Remix)", "yourai", 144, "https://archive.org/download/mhrk063/mhrk063_yourai_02_my_heart_is_so_loud__adrian_juarez_remix.mp3", "https://archive.org/download/mhrk063/mhrk063.jpg"),
    Track("t3", "My Heart Is So Loud (303 Unlimited Remix)", "yourai", 332, "https://archive.org/download/mhrk063/mhrk063_yourai_03_my_heart_is_so_loud__303_unlimited_remix.mp3", "https://archive.org/download/mhrk063/mhrk063.jpg"),
    // aAirial — Wanderings [mhrk207]
    Track("t4", "Towards New Horizons", "aAirial", 202, "https://archive.org/download/mhrk207/aAirial%20-%20Wanderings%20-%2001%20Towards%20New%20Horizons.mp3", "https://archive.org/download/mhrk207/cover.jpg"),
    Track("t5", "Where", "aAirial", 275, "https://archive.org/download/mhrk207/aAirial%20-%20Wanderings%20-%2002%20Where.mp3", "https://archive.org/download/mhrk207/cover.jpg"),
    Track("t6", "One Point Is Enough to Define a Space", "aAirial", 323, "https://archive.org/download/mhrk207/aAirial%20-%20Wanderings%20-%2003%20One%20Point%20is%20Enough%20to%20Define%20a%20Space.mp3", "https://archive.org/download/mhrk207/cover.jpg"),
    // Sai — Random Motion Of Particles [mhrk217]
    Track("t7", "And the Light Was Gone", "Sai", 325, "https://archive.org/download/mhrk217/Sai%20-%20Random%20Motion%20Of%20Particles%20-%2001%20And%20the%20light%20was%20gone.mp3", "https://archive.org/download/mhrk217/mhrk217.jpg"),
    Track("t8", "(Under the Surface)", "Sai", 166, "https://archive.org/download/mhrk217/Sai%20-%20Random%20Motion%20Of%20Particles%20-%2002%20%28Under%20rhe%20surface%29.mp3", "https://archive.org/download/mhrk217/mhrk217.jpg"),
    Track("t9", "A Reality Like Water", "Sai", 285, "https://archive.org/download/mhrk217/Sai%20-%20Random%20Motion%20Of%20Particles%20-%2003%20A%20reality%20like%20water.mp3", "https://archive.org/download/mhrk217/mhrk217.jpg"),
    // Polyphonics — Secret Silence [mhrk176]
    Track("t10", "Far and Away", "Polyphonics", 335, "https://archive.org/download/mhrk176/mhrk176%20Polyphonics%20-%20Secret%20Silence%20-%2001%20Far%20and%20Away.mp3", "https://archive.org/download/mhrk176/mhrk176.jpg"),
    Track("t11", "Harmonics", "Polyphonics", 235, "https://archive.org/download/mhrk176/mhrk176%20Polyphonics%20-%20Secret%20Silence%20-%2002%20Harmonics.mp3", "https://archive.org/download/mhrk176/mhrk176.jpg"),
    Track("t12", "Something and Sometimes", "Polyphonics", 158, "https://archive.org/download/mhrk176/mhrk176%20Polyphonics%20-%20Secret%20Silence%20-%2003%20Something%20and%20Sometimes.mp3", "https://archive.org/download/mhrk176/mhrk176.jpg"),
    // Kanz — Remind Me Tomorrow [mhrk272]
    Track("t13", "Restless", "Kanz", 370, "https://archive.org/download/mhrk272/Kanz%20-%20Remind%20me%20Tomorrow%20-%2001%20Restless.mp3", "https://archive.org/download/mhrk272/mhrk272.jpg"),
    Track("t14", "Dark Places", "Kanz", 480, "https://archive.org/download/mhrk272/Kanz%20-%20Remind%20me%20Tomorrow%20-%2002%20Dark%20Places.mp3", "https://archive.org/download/mhrk272/mhrk272.jpg"),
    Track("t15", "Eden's Whales", "Kanz", 399, "https://archive.org/download/mhrk272/Kanz%20-%20Remind%20me%20Tomorrow%20-%2003%20Eden%27s%20Whales.mp3", "https://archive.org/download/mhrk272/mhrk272.jpg"),
)

internal val COLLECTIONS: List<Collection> = listOf(
    Collection("mhrk063", "my heart is so loud", "Album • yourai", CollectionKind.ALBUM, 0xFF1DB954, "https://archive.org/download/mhrk063/mhrk063.jpg", listOf("t1", "t2", "t3")),
    Collection("mhrk207", "Wanderings", "Album • aAirial", CollectionKind.ALBUM, 0xFF5B4DBE, "https://archive.org/download/mhrk207/cover.jpg", listOf("t4", "t5", "t6")),
    Collection("mhrk217", "Random Motion of Particles", "Album • Sai", CollectionKind.ALBUM, 0xFF7E3FF2, "https://archive.org/download/mhrk217/mhrk217.jpg", listOf("t7", "t8", "t9")),
    Collection("mhrk176", "Secret Silence", "Album • Polyphonics", CollectionKind.ALBUM, 0xFFE0A23B, "https://archive.org/download/mhrk176/mhrk176.jpg", listOf("t10", "t11", "t12")),
    Collection("mhrk272", "Remind Me Tomorrow", "Album • Kanz", CollectionKind.ALBUM, 0xFF128E7C, "https://archive.org/download/mhrk272/mhrk272.jpg", listOf("t13", "t14", "t15")),
)

internal val SECTIONS: List<Section> = listOf(
    Section("Made for you", listOf("mhrk207", "mhrk176", "mhrk272")),
    Section("New albums", listOf("mhrk063", "mhrk217", "mhrk207")),
    Section("Ambient & electronica", listOf("mhrk272", "mhrk176", "mhrk063")),
)

internal val GENRES: List<Genre> = listOf(
    Genre("Electronic", 0xFF7E3FF2), Genre("Ambient", 0xFF1DB954), Genre("IDM", 0xFF1E3264),
    Genre("Techno", 0xFFE13300), Genre("Downtempo", 0xFF128E7C), Genre("Breaks", 0xFFE0A23B),
    Genre("Glitch", 0xFF8D67AB), Genre("Bass", 0xFFE1118C),
)
