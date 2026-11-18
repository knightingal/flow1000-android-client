package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
    SharedTransitionLayout {
      val navController: NavHostController = rememberNavController()
      NavHost(
        navController, startDestination = "flow1000Home",
      ) {
        composable("flow1000Home") {
          TabMainScreen(navController,
            this@SharedTransitionLayout,
            this@composable
          ).MainScreen(
            modifier = modifier,
          )
        }
        composable<ExistSectionGridPage.ExistAlbumCoverScreenRoute> {
          ExistSectionGridPage(navController,
            this@SharedTransitionLayout,
            this@composable
          ).ExistAlbumCoverScreen(it.toRoute<ExistSectionGridPage.ExistAlbumCoverScreenRoute>().album)
        }
        composable<SectionImageListPage.SectionImageListScreenRoute> {
          val route = it.toRoute<SectionImageListPage.SectionImageListScreenRoute>()
          SectionImageListPage(
            this@SharedTransitionLayout,
            this@composable
          ).SectionImageListScreen(
              route.sectionName,
              route.sectionId
            )
        }
        composable<OnlineSectionGridPage.OnlineSectionGridScreenRoute> {
          val route = it.toRoute<OnlineSectionGridPage.OnlineSectionGridScreenRoute>()
          OnlineSectionGridPage(navController = navController,
            this@SharedTransitionLayout,
            this@composable
          ).OnlineSectionGridScreen(
            route.album,
            route.albumSourcePath
          )
        }
        composable<OnlineSectionImageListPage.OnlineSectionImageListScreenRoute> {
          val route = it.toRoute<OnlineSectionImageListPage.OnlineSectionImageListScreenRoute>()
          OnlineSectionImageListPage(
            this@SharedTransitionLayout,
            this@composable
          ).SectionImageListScreen(
            name = route.sectionName,
            id = route.sectionId,
            albumSourcePath = route.albumSourcePath
          )
        }
      }
    }
  }

  enum class Destination(
    val label: String,
  ) {
    ALL("All"),
    Exist("Exist"),
    Pending("Pending"),
  }
}