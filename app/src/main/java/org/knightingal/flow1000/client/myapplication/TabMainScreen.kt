package org.knightingal.flow1000.client.myapplication

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.myapplication.TabMainActivity.Destination
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSharedTransitionApi::class)
class TabMainScreen(
  val navController: NavHostController,
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun MainScreen(
    modifier: Modifier = Modifier,
  ) {
    val picSectionDao = App.db.picSectionDao()

    val allPicSectionBeanList = picSectionDao.getAll().toList()
      .map { bean -> PicSectionData(bean, 0).apply { this.process = 0 } }

    val existAlbumCoverList: List<PicSectionBean> = listOf(
      "1000", "1803", "1804", "1805",
      "1806", "1807", "1808"
    ).mapNotNull { albumName ->
      picSectionDao.getExistAlbumCover(albumName)
    }.map { it }

    Scaffold(modifier = modifier, topBar = {
      TopAppBar(
        title = { Text("Flow1000 Client") }
      )
    }) { contentPadding ->
      Column(modifier = Modifier.padding(contentPadding)) {
        val pagerState = rememberPagerState(pageCount = { 3 })
        val coroutineScope = rememberCoroutineScope()
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
          Destination.entries.forEachIndexed { index, destination ->
            Tab(
              selected = pagerState.currentPage == index,
              onClick = {
                coroutineScope.launch {
                  pagerState.animateScrollToPage(index)
                }
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
        AdvancePager(pagerState, allPicSectionBeanList, existAlbumCoverList)
      }
    }
  }

  @Composable
  fun AdvancePager(
    pagerState: PagerState,
    allPicSectionBeanList: List<PicSectionData>,
    existAlbumCoverList: List<PicSectionBean>,
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      HorizontalPager(
        state = pagerState
      ) { page ->
        when (page) {
          0 -> AllListScreen(allPicSectionBeanList = allPicSectionBeanList)
          1 -> ExistAlbumCoverTab(albumCoverList = existAlbumCoverList)
          2 -> PlaylistScreen()
        }
      }
    }
  }

  @Composable
  fun AllListScreen(allPicSectionBeanList: List<PicSectionData>) {

    val initMap: Map<Long, Int> = mapOf (1L to 1, 2L to 2, 3L to 3)

    val progressionState: MutableMap<Long, Int> = remember { mutableStateMapOf() }

    progressionState.putAll(initMap)

    LaunchedEffect(progressionState) {
      while (true) {
        delay(1.seconds)
        val currV = progressionState[1L] ?: 0
        progressionState[1L] = currV + 1
      }
    }

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      LazyColumn {
        items(allPicSectionBeanList) { picSectionData ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            val progression = progressionState[picSectionData.picSectionBean.id]
            Text(picSectionData.picSectionBean.name + ": " + progression, modifier = Modifier.padding(16.dp))
          }
        }
      }
    }
  }

  @OptIn(ExperimentalSharedTransitionApi::class)
  @Composable
  fun ExistAlbumCoverTab(albumCoverList: List<PicSectionBean>) {
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
        items(albumCoverList) { picSectionBean ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            AlbumCoverCard(
              sharedTransitionScope = sharedTransitionScope,
              animatedContentScope = animatedContentScope,
              albumCover = ExistAlbumCover(
                name = picSectionBean.name,
                coverWidth = picSectionBean.coverWidth,
                coverHeight = picSectionBean.coverHeight,
                cover = picSectionBean.cover ?: ""
              ),
              sharedKey = "album_${picSectionBean.album}"
            ) {
              navController.navigate(ExistSectionGridPage.ExistAlbumCoverScreenRoute(album = picSectionBean.album!!))
            }
          }
        }
      }
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

}