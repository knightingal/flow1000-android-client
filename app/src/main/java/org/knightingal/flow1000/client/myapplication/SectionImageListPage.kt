package org.knightingal.flow1000.client.myapplication

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.beans.PicInfoBean

@OptIn(ExperimentalSharedTransitionApi::class)
class SectionImageListPage(
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {

  @Serializable
  data class SectionImageListScreenRoute(val sectionName: String, val sectionId: Long)

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun SectionImageListScreen(sectionName: String, sectionId: Long) {
    val picInfoDao = App.db.picInfoDao()
    val picList: List<PicInfoBean> = picInfoDao.queryBySectionInnerIndex(sectionId)
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
      TopAppBar(
        title = { Text(sectionName, overflow = TextOverflow.Ellipsis) },
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
              sharedContentState = sharedTransitionScope.rememberSharedContentState("section_$sectionId"),
              animatedVisibilityScope = animatedContentScope,
              enter = fadeIn(),
              exit = fadeOut(),
            )
            .padding(contentPadding)
        },

        contentAlignment = Alignment.Center,
      ) {
        val lazyStaggeredGridState = rememberLazyListState()
        LazyColumn(
          state = lazyStaggeredGridState,
          contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
          modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)
        ) {
          items(picList) { picInfoBean ->
            Box(
              modifier = Modifier.fillMaxWidth(),
            ) {
              FitSizeImageCard(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                cardCover = ExistAlbumCover(
                  name = sectionName,
                  coverWidth = picInfoBean.width,
                  coverHeight = picInfoBean.height,
                  cover = picInfoBean.name
                )
              )
            }
          }
        }
      }
    }
  }


  @Composable
  fun FitSizeImageCard(
    cardCover: CardCover<*>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 2.dp, bottom = 2.dp),
      contentAlignment = Alignment.Center
    ) {
      val maxWidth = constraints.maxWidth

      val targetWidth: Int = if (maxWidth > cardCover.width) {
        cardCover.width
      } else {
        maxWidth
      }
      with(sharedTransitionScope) {
        Card(
          border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
          colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.LightGray,
          ),
          modifier = Modifier
            .sharedElement(
              sharedContentState = sharedTransitionScope.rememberSharedContentState(cardCover.coverUri as String),
              animatedVisibilityScope = animatedContentScope
            )
            .width(targetWidth.dp).wrapContentSize(),
        ) {
          ImageContentInCard(cardCover)
        }
      }
    }
  }

}