package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme


class TabMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Flow1000clientTheme {
                NavigationTabExample(modifier = Modifier.fillMaxSize())
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun NavigationTabExample(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        val startDestination = Destination.SONGS
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Scaffold(modifier = modifier, topBar = {
            TopAppBar(
                title = { Text("Flow1000 Client") }
            )
        }) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                PrimaryTabRow(selectedTabIndex = selectedDestination) {
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
    }


    @Composable
    fun AllListScreen() {
        Box(
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {
                items(100) { index ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Item #$index", modifier = Modifier.padding(16.dp))
                    }
                }
            }
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
        SONGS("allList", "All"),
        ALBUM("existList", "Exist"),
        PLAYLISTS("pendingList", "Pending"),
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
                        Destination.SONGS -> AllListScreen()
                        Destination.ALBUM -> AlbumScreen()
                        Destination.PLAYLISTS -> PlaylistScreen()
                    }
                }
            }
        }
    }
}