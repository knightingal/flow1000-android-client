package org.knightingal.flow1000.client.myapplication

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSharedTransitionApi::class)
class ExistSectionGridPage(
  val navController: NavHostController,
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {
  @Serializable
  data class ExistAlbumCoverScreenRoute(val album: String)

  @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
  @Composable
  fun ExistAlbumCoverScreen(album: String) {
    val picSectionDao = App.db.picSectionDao()
    val existSectionList = picSectionDao.getAllExistByAlbum(album)

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
      TopAppBar(
        title = { Text(album) },
        colors = topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      )
    }) { contentPadding ->
      Box(
        modifier = with(sharedTransitionScope) {
          Modifier
            .sharedBounds(
              sharedContentState = sharedTransitionScope.rememberSharedContentState("album_$album"),
              animatedVisibilityScope = animatedContentScope!!,
              enter = fadeIn(),
              exit = fadeOut(),
            )
            .padding(contentPadding)
        },
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
          items(existSectionList) { picSectionBean ->
            Box(
              modifier = Modifier.fillMaxWidth(),
            ) {
              AlbumCoverCard(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                sharedKey = "section_${picSectionBean.id}",
                albumCover = ExistAlbumCover(
                  name = picSectionBean.name,
                  coverWidth = picSectionBean.coverWidth,
                  coverHeight = picSectionBean.coverHeight,
                  cover = picSectionBean.cover ?: ""
                )
              ) {
                navController.navigate(SectionImageListPage.SectionImageListScreenRoute(
                  sectionName = picSectionBean.name,
                  sectionId = picSectionBean.id
                ))
              }
            }
          }
        }
      }
    }
  }
}