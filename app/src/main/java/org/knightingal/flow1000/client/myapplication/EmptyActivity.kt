package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.knightingal.flow1000.client.R
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme

class EmptyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Flow1000clientTheme {
                NavigationTabExample(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Flow1000clientTheme {
        Greeting("Android")
    }
}

@Composable
fun SongsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Songs Screen")
    }
}

@Composable
fun AlbumScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Album Screen")
    }
}

@Composable
fun PlaylistScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Playlist Screen")
    }
}

enum class Destination(
    val route: String,
    val label: String,
) {
    SONGS("songs", "Songs"),
    ALBUM("album", "Album"),
    PLAYLISTS("playlist", "Playlist")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.SONGS -> SongsScreen()
                    Destination.ALBUM -> AlbumScreen()
                    Destination.PLAYLISTS -> PlaylistScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun NavigationTabExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = modifier) { contentPadding ->
        PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
            Destination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        AppNavHost(navController, startDestination)
    }
}


interface CardCover<T> {
    val width: Int
    val height: Int
    val coverUri: T
    val name: String
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumCoverCard(
    albumCover: CardCover<*>,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    appendContent: @Composable () -> Unit = {},
    edit: Boolean = false,
    onClick: () -> Unit = {},
) {
    val modifier = if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                state = sharedTransitionScope.rememberSharedContentState(albumCover.coverUri as String),
                animatedVisibilityScope = animatedContentScope!!
            )
        }
    } else {
        Modifier
    }

    Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.LightGray,
        ), modifier = modifier
            .fillMaxWidth().wrapContentSize(),
        onClick = onClick
    ) {
        ImageContentInCard(albumCover)
        if (edit) {
            OutlinedTextField(
                readOnly = true,
                colors = TextFieldDefaults.colors(

                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth(),
                value = albumCover.name,
                onValueChange = {}
            )
        } else {
            Text(albumCover.name, modifier = Modifier.padding(16.dp))
        }
        appendContent()
    }
}

@Composable
fun ImageContentInCard(
    cardCover: CardCover<*>,
) {
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(
            ratio = cardCover.width.toFloat()
                    / cardCover.height.toFloat()
        )
    ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = painterResource(cardCover.coverUri as Int),
                contentDescription = null,
            )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
fun AlbumCoverCardEditPreview() {
    Flow1000clientTheme {
        AlbumCoverCard(
            albumCover = object : CardCover<Int> {
                override val width: Int
                    get() = 640
                override val height: Int
                    get() = 426
                override val coverUri: Int
                    get() = R.drawable.fc6_nightly_wind_down
                override val name: String
                    get() = "EditAI20220605211354"
            },
            edit = true
        )
    }
}
