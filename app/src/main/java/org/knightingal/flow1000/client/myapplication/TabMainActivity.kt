package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme


class TabMainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      Flow1000clientTheme {
        NavigationTab(modifier = Modifier.fillMaxSize())
      }
    }
  }

  @Composable
  fun NavigationTab(
    modifier: Modifier = Modifier,
  ) {
    val navController: NavHostController = rememberNavController()
    NavHost(
      navController, startDestination = "flow1000Home",
    ) {
      composable("flow1000Home") {
        TabMainScreen(navController).MainScreen(
          modifier = modifier,
        )
      }
      composable<ExistSectionGridPage.ExistAlbumCoverScreenRoute> {
        ExistSectionGridPage(navController)
          .ExistAlbumCoverScreen(it.toRoute<ExistSectionGridPage.ExistAlbumCoverScreenRoute>().album)
      }
    }
  }

  enum class Destination(
    val label: String,
  ) {
    SONGS("All"),
    ALBUM("Exist"),
    PLAYLISTS("Pending"),
  }
}