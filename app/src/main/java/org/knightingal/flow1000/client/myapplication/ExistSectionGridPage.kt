package org.knightingal.flow1000.client.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.myapplication.TabMainActivity.ExistAlbumCover
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme

class ExistSectionGridPage : ComponentActivity() {

  private lateinit var picSectionDao: PicSectionDao


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    picSectionDao = App.db.picSectionDao()
    val existSectionList = picSectionDao.getAllExistByAlbum(intent.getStringExtra("album") ?: "1000")
    enableEdgeToEdge()
    setContent {
      Flow1000clientTheme {
        ExistAlbumCoverScreen(albumCoverList = existSectionList)
      }
    }
  }

  @OptIn(ExperimentalSharedTransitionApi::class)
  @Composable
  fun ExistAlbumCoverScreen(albumCoverList: List<PicSectionBean>) {
    val context = LocalContext.current
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
              albumCover = ExistAlbumCover(
                name = picSectionBean.name,
                coverWidth = picSectionBean.coverWidth,
                coverHeight = picSectionBean.coverHeight,
                cover = picSectionBean.cover ?: ""
              )
            ) {
              context.startActivity(
                Intent(context, SectionImageListPage::class.java).apply {
                  putExtra("sectionId", picSectionBean.id)
                  putExtra("sectionName", picSectionBean.name)
                }
              )
            }
          }
        }
      }
    }
  }
}