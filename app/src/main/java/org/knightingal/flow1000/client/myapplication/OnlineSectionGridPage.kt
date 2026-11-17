package org.knightingal.flow1000.client.myapplication

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_IP
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_PORT

@Serializable
data class Flow1000AlbumPageParam(val name: String, val albumSourcePath: String)

class OnlineSectionGridPage(
  val navController: NavHostController,
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {
  @Serializable
  data class OnlineSectionGridScreenRoute(val album: String, val albumSourcePath: String)

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun OnlineSectionGridScreen(albumConfig: Flow1000AlbumPageParam) {
    // Implementation for the online section grid screen
    val pinIndexList = remember { mutableStateListOf<PicIndexItem>() }
    LaunchedEffect(Unit) {
      CoroutineScope(Dispatchers.IO).launch {
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?album=${albumConfig.name}"
        val client = HttpClient(CIO)
        val response: String = client.get(stringUrl).body()
        val picIndexList1 = Gson().fromJson(response, Array<PicIndexItem>::class.java)
        pinIndexList.addAll(picIndexList1)
      }
    }



    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
      TopAppBar(
        title = { Text(albumConfig.name) },
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
              sharedContentState = sharedTransitionScope.rememberSharedContentState("album_${albumConfig.name}"),
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
          contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
          verticalItemSpacing = 4.dp,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 0.dp),
        ) {
          items(pinIndexList.size) { index ->
            val picIndex = pinIndexList[index]
            val cover = OnlineSectionCover(
              name = picIndex.name,
              width = picIndex.coverWidth,
              height = picIndex.coverHeight,
              coverUri = "http://${SERVER_IP}:${SERVER_PORT}/linux1000/${albumConfig.albumSourcePath}/${picIndex.name}/${
                picIndex.cover.replace(".bin", "")
              }"
            )
            AlbumCoverCard(
              albumCover = cover,
              sharedTransitionScope = sharedTransitionScope,
              animatedContentScope = animatedContentScope,
              edit = false,
              appendContent = {
              }
            ) {
              navController.navigate(
                route = OnlineSectionImageListPage.OnlineSectionImageListScreenRoute(
                  picIndex.name,
                  picIndex.index,
                  albumConfig.albumSourcePath
                )
              )
            }
          }
        }
      }
    }
  }
}

class OnlineSectionCover (
  override val name: String,
  override val width: Int,
  override val height: Int,
  override val coverUri: String
)
: CardCover<String>


enum class ClientStatus {
  NONE, PENDING, LOCAL
}
class PicIndexItem(
  val index: Long,
  val name: String,
  val coverWidth: Int,
  val coverHeight: Int,
  val cover: String,
  val clientStatus: ClientStatus,
)