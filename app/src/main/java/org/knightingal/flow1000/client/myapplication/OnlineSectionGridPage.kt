package org.knightingal.flow1000.client.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.services.DownloadService
import org.knightingal.flow1000.client.util.EnvArgs.Companion.PROTOCOL_PREFIX
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_IP
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_PORT

class OnlineSectionGridPage(
  val navController: NavHostController,
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {
  @Serializable
  data class OnlineSectionGridScreenRoute(val album: String, val albumSourcePath: String)

  suspend fun subscribeSectionById(id: Long) {
    val stringUrl = "${PROTOCOL_PREFIX}//${SERVER_IP}:${SERVER_PORT}/local1000/downloadSection?id=${id}"
    val client = HttpClient(CIO)
    val response: String = client.post(stringUrl).body()
    Log.d("OnlineSectionGridPage", "subscribeSectionById response: $response")
  }

  fun scheduleDownloadSectionTask(ctx: Context, picSectionBean: PicSectionBean) {
    Log.d("OnlineSectionGridPage", "scheduleDownloadSectionTask: picSectionBean id=${picSectionBean.id}, name=${picSectionBean.name}")

    val conn = object : ServiceConnection {
      override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("OnlineSectionGridScreen", "Service connected")
        val binder = service as DownloadService.LocalBinder
        val downloadService = binder.getService()
        downloadService.processPendingSectionItem(PicSectionData(picSectionBean, totalCount = 0))
        ctx.unbindService(this)
      }

      override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("OnlineSectionGridScreen", "Service disconnected")
      }
    }

    ctx.bindService(Intent(ctx, DownloadService::class.java),
      conn,
      Context.BIND_AUTO_CREATE
    )
  }

  suspend fun fetchSectionIndexList(album: String): List<PicSectionBean> {
    val stringUrl = "${PROTOCOL_PREFIX}//${SERVER_IP}:${SERVER_PORT}/local1000/picIndexAjax?album=${album}"
    val client = HttpClient(CIO)
    val response: String = client.get(stringUrl).body()
    return Gson().fromJson(response, Array<PicSectionBean>::class.java).toList()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun OnlineSectionGridScreen(
    album: String,
    albumSourcePath: String
  ) {
    val ctx = LocalContext.current
    val pinIndexList = remember { mutableStateListOf<PicSectionBean>() }
    LaunchedEffect(Unit) {
      CoroutineScope(Dispatchers.IO).launch {
        pinIndexList.addAll(fetchSectionIndexList(album))
      }
    }

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
              sharedContentState = sharedTransitionScope.rememberSharedContentState("album_${album}"),
              animatedVisibilityScope = animatedContentScope,
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
          items(pinIndexList) { picIndex ->
            val cover = OnlineSectionCover(
              name = picIndex.name,
              width = picIndex.coverWidth,
              height = picIndex.coverHeight,
              coverUri = "${PROTOCOL_PREFIX}//${SERVER_IP}:${SERVER_PORT}/linux1000/${albumSourcePath}/${picIndex.name}/${
                picIndex.cover?.replace(".bin", "")
              }"
            )
            AlbumCoverCard(
              albumCover = cover,
              sharedTransitionScope = sharedTransitionScope,
              animatedContentScope = animatedContentScope,
              edit = false,
              appendContent = {
                Row {
                  IconButton(onClick = {
                    scheduleDownloadSectionTask(ctx, picIndex)
                    CoroutineScope(Dispatchers.IO).launch {
                      subscribeSectionById(picIndex.id)
                      fetchSectionIndexList(album).let { newList ->
                        pinIndexList.clear()
                        pinIndexList.addAll(newList)
                      }
                    }
                  }) {
                    val iconVec = if (picIndex.clientStatus == PicSectionBean.ClientStatus.NONE)
                      Icons.Outlined.FavoriteBorder
                    else
                      Icons.Outlined.Favorite
                    Icon(
                      imageVector = iconVec,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurface
                    )
                  }

                }
              }
            ) {
              navController.navigate(
                route = OnlineSectionImageListPage.OnlineSectionImageListScreenRoute(
                  picIndex.name,
                  picIndex.id,
                  albumSourcePath
                )
              )
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
  ): CardCover<String>
}
