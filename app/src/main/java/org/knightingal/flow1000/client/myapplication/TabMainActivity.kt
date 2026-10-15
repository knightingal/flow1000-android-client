package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme


class TabMainActivity : ComponentActivity() {

  private lateinit var picSectionDao: PicSectionDao
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    picSectionDao = App.db.picSectionDao()

    val allPicSectionBeanList = picSectionDao.getAll().toList()
      .map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }

    val existSectionBean: List<PicSectionBean> = picSectionDao.getAllExistByAlbum("1000")

    enableEdgeToEdge()
    setContent {
      Flow1000clientTheme {
        NavigationTab(modifier = Modifier.fillMaxSize(), allPicSectionBeanList, existSectionBean)
      }
    }
  }


  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun NavigationTab(modifier: Modifier = Modifier, allPicSectionBeanList: List<PicSectionData>, existSectionBean: List<PicSectionBean>) {
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
        AppNavHost(selectedDestination, allPicSectionBeanList, existSectionBean)

      }
    }
  }


  @Composable
  fun AllListScreen(allPicSectionBeanList: List<PicSectionData>) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      LazyColumn {
        items(allPicSectionBeanList) { picSectionData ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(picSectionData.picSectionBean.name, modifier = Modifier.padding(16.dp))
          }
        }
      }
    }
  }

  @OptIn(ExperimentalSharedTransitionApi::class)
  @Composable
  fun ExistListScreen(existSectionBean: List<PicSectionBean>) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      val lazyStaggeredGridState = rememberLazyStaggeredGridState()
      LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize(),
      ) {
        items(existSectionBean) { picSectionBean ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            AlbumCoverCard(
              albumCover = ExistAlbumCover(
                name = picSectionBean.name,
                coverWidth = picSectionBean.coverWidth,
                coverHeight = picSectionBean.coverHeight,
                cover = picSectionBean.cover ?: ""
              )
            )
          }
        }
      }
    }
  }

  @Serializable
  class ExistAlbumCover(
    override val name: String,
    val coverWidth: Int,
    val coverHeight: Int,
    val cover: String,
  ): CardCover<String> {
    override val width: Int = coverWidth
    override val height: Int = coverHeight
    override val coverUri: String = "/${name}/${cover}"
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
      Destination.ALBUM.ordinal -> ExistListScreen(existSectionBean)
      Destination.PLAYLISTS.ordinal -> PlaylistScreen()
    }
  }

  @Preview(showBackground = true, showSystemUi = true)
  @Composable
  fun NavigationTabPreview() {
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

    NavigationTab(modifier = Modifier.fillMaxSize(), previewList, emptyList())
  }
}