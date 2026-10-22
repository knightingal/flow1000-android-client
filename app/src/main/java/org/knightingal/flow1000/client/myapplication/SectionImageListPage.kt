package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.unit.dp
import org.knightingal.flow1000.client.beans.PicInfoBean
import org.knightingal.flow1000.client.dao.PicInfoDao
import org.knightingal.flow1000.client.dao.PicSectionDao
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme

class SectionImageListPage : ComponentActivity() {

  private lateinit var picSectionDao: PicSectionDao
  private lateinit var picInfoDao: PicInfoDao


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    picSectionDao = App.db.picSectionDao()
    picInfoDao = App.db.picInfoDao()
    val picList: List<PicInfoBean> = picInfoDao.queryBySectionInnerIndex(intent.getLongExtra("sectionId", 1))
    val sectionName: String = intent.getStringExtra("sectionName") ?: ""
    enableEdgeToEdge()
    setContent {
      Flow1000clientTheme {
        ExistAlbumCoverScreen(sectionName = sectionName, picList = picList)
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun ExistAlbumCoverScreen(sectionName: String, picList: List<PicInfoBean>) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
      TopAppBar(
        title = { Text(sectionName) },
        colors = topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      )
    }) { contentPadding ->
      Box(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentAlignment = Alignment.Center
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

      Card(
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = CardColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          containerColor = MaterialTheme.colorScheme.surface,
          disabledContainerColor = Color.LightGray,
          disabledContentColor = Color.LightGray,
        ),
        modifier = Modifier.width(targetWidth.dp).wrapContentSize(),
      ) {
        ImageContentInCard(cardCover)
      }
    }
  }

}