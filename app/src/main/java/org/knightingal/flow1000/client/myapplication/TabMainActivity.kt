package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()
    NavHost(
      navController, startDestination = "flow1000Home",
    ) {
      composable("flow1000Home") {
        TabMainScreen(
          modifier = modifier,
        )
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

  @Composable
  fun AppNavHost(
    selectedDestination: Int,
    allPicSectionBeanList: List<PicSectionData>,
    existSectionBean: List<PicSectionBean>
  ) {
    when (selectedDestination) {
      Destination.SONGS.ordinal -> AllListScreen(allPicSectionBeanList)
      Destination.ALBUM.ordinal -> ExistAlbumCoverScreen(albumCoverList = existSectionBean)
      Destination.PLAYLISTS.ordinal -> PlaylistScreen()
    }
  }





  @Preview(showBackground = true, showSystemUi = true)
  @Composable
  fun NavigationTabPreview() {
//    val previewList = listOf(
//      PicSectionData(
//        PicSectionBean(
//          name = "Section 1",
//          id = 0,
//          clientStatus = PicSectionBean.ClientStatus.PENDING
//        ),
//        totalCount = 0
//      ),
//      PicSectionData(
//        PicSectionBean(
//          name = "Section 2",
//          id = 1,
//          clientStatus = PicSectionBean.ClientStatus.PENDING
//        ),
//        totalCount = 0
//      )
//    )

    NavigationTab(modifier = Modifier.fillMaxSize())
  }

  @Preview(showBackground = true, showSystemUi = true)
  @Composable
  fun NavHostPreview() {

    val previewList = listOf(
      PicSectionData(
        PicSectionBean(
          name = "Section 1",
          id = 0,
          clientStatus = PicSectionBean.ClientStatus.PENDING
        ),
        totalCount = 0
      ),
      PicSectionData(
        PicSectionBean(
          name = "Section 2",
          id = 1,
          clientStatus = PicSectionBean.ClientStatus.PENDING
        ),
        totalCount = 0
      )
    )
    AppNavHost(
      selectedDestination = Destination.SONGS.ordinal,
      allPicSectionBeanList = previewList,
      existSectionBean = emptyList()
    )
  }
}