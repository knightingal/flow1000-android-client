package org.knightingal.flow1000.client.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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
import org.knightingal.flow1000.client.beans.PicSectionBean
import org.knightingal.flow1000.client.beans.PicSectionData
import org.knightingal.flow1000.client.myapplication.TabMainActivity.Destination
import org.knightingal.flow1000.client.services.DownloadService
import org.knightingal.flow1000.client.services.ProcessCounter
import org.knightingal.flow1000.client.services.SimpleService
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_IP
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_PORT
import org.nanjing.knightingal.processerlib.RefreshListener
import kotlin.String
import kotlin.text.replace

class TabMainScreen(
  val navController: NavHostController,
  val sharedTransitionScope: SharedTransitionScope,
  val animatedContentScope: AnimatedContentScope
) {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun ServiceBindScreen() {
    val ctx = LocalContext.current

    val progressionState: MutableMap<Int, String> = remember { mutableStateMapOf() }

    DisposableEffect(Unit) {
      var downloadService: DownloadService?
      val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
          val binder = service as DownloadService.LocalBinder
          downloadService = binder.getService()
          downloadService?.setRefreshListener(object : RefreshListener {
            override fun doRefreshProcess(sectionId: Long, finish: Boolean) {
              Log.d("TabMainScreen", "doRefreshProcess: sectionId=$sectionId, finish=$finish")
              // Update the progressionState map with the new progress
              val progressMessage = if (finish) {
                "Finished"
              } else {
                "In Progress"
              }
              progressionState[sectionId.toInt()] = progressMessage
            }

            override fun doRefreshList(picSectionBeanList: List<PicSectionData>) {
              Log.d("TabMainScreen", "doRefreshList: picSectionBeanList size=${picSectionBeanList.size}")
            }

            override fun notifyListReady() {
              Log.d("TabMainScreen", "notifyListReady called")
            }


          })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          // Handle service disconnection if needed
          Log.d("TabMainScreen", "Service disconnected")
          downloadService = null
        }
      }

      ctx.bindService(Intent(ctx, DownloadService::class.java),
        conn,
        Context.BIND_AUTO_CREATE
      )

      onDispose {
        Log.d("TabMainScreen", "Unbinding service")
        ctx.unbindService(conn)
      }
    }

    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { TopAppBar(title = { Text("Service Bind Screen") }) }
    ) { contentPadding ->
      Box(
        modifier = Modifier.padding(contentPadding),
        contentAlignment = Alignment.Center
      ) {
        LazyColumn {
          items(SimpleService.TASK_COUNT) { index ->
            Text(
              "$index: ${progressionState[index] ?: "Not started"}",
              modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
          }
        }
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun MainScreen(
    modifier: Modifier = Modifier,
  ) {
    val picSectionDao = App.db.picSectionDao()

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
        AdvancePager(pagerState, existAlbumCoverList)
      }
    }
  }

  @Composable
  fun AdvancePager(
    pagerState: PagerState,
    existAlbumCoverList: List<PicSectionBean>,
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      HorizontalPager(
        state = pagerState
      ) { page ->
        when (page) {
          0 -> AllListScreen()
          1 -> ExistAlbumCoverTab(albumCoverList = existAlbumCoverList)
          2 -> PendingListScreen()
        }
      }
    }
  }

  @Composable
  fun AllListScreen() {
    val albumConfigListState = remember { mutableStateListOf<AlbumConfig>() }

    LaunchedEffect(Unit) {
      CoroutineScope(Dispatchers.IO).launch {
        val stringUrl = "http://${SERVER_IP}:${SERVER_PORT}/local1000/albumConfig/list"
        val client = HttpClient(CIO)
        val response: String = client.get(stringUrl).body()
        val albumConfigList = Gson().fromJson(response, Array<AlbumConfig>::class.java)
        albumConfigListState.addAll(albumConfigList)
      }
    }

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
        items(albumConfigListState) { albumConfig ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            val coverUri = "http://${SERVER_IP}:${SERVER_PORT}/linux1000/${
              albumConfig.sourcePath
            }/${
              albumConfig.coverSection.dirName
            }/${
              albumConfig.coverSection.cover.replace(".bin", "")
            }"
            AlbumCoverCard(
              sharedTransitionScope = sharedTransitionScope,
              animatedContentScope = animatedContentScope,
              albumCover = OnlineAlbumCover(
                name = albumConfig.name,
                coverWidth = albumConfig.coverSection.coverWidth,
                coverHeight = albumConfig.coverSection.coverHeight,
                coverUri = coverUri
              ),
              sharedKey = "album_config_${albumConfig.name}"
            ) {
//              navController.navigate(ExistSectionGridPage.ExistAlbumCoverScreenRoute(album = picSectionBean.album!!))
            }
          }
        }
      }
    }
  }

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
  fun PendingListScreen() {
    val pendingList = remember { mutableStateListOf<PicSectionData>() }
    val ctx = LocalContext.current
    val progressMap = remember { mutableStateMapOf<Long, Int>() } // Map to hold progress for each sectionId

    DisposableEffect(Unit) {
      var downloadService: DownloadService? = null

      val listener = object : RefreshListener {
        override fun doRefreshProcess(sectionId: Long, finish: Boolean) {
          Log.d("TabMainScreen", "doRefreshProcess: sectionId=$sectionId, finish=$finish")
          val counter = ProcessCounter.getCounter(sectionId)
          if (counter != null) {
            progressMap[sectionId] = counter.getProcess()
          }
        }

        override fun doRefreshList(picSectionBeanList: List<PicSectionData>) {
          Log.d("TabMainScreen", "doRefreshList: picSectionBeanList size=${picSectionBeanList.size}")
          pendingList.clear()
          pendingList.addAll(picSectionBeanList)
        }

        override fun notifyListReady() {
          Log.d("TabMainScreen", "notifyListReady called")
        }
      }

      val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
          val binder = service as DownloadService.LocalBinder
          downloadService = binder.getService()

          downloadService?.setRefreshListener(listener)
          progressMap.putAll(ProcessCounter.getAllCounter())
          pendingList.addAll(downloadService?.getPendingSectionList() ?: emptyList())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
          // Handle service disconnection if needed
          Log.d("TabMainScreen", "Service disconnected")
          downloadService?.removeRefreshListener(listener)
          downloadService = null
        }
      }

      ctx.bindService(Intent(ctx, DownloadService::class.java),
        conn,
        Context.BIND_AUTO_CREATE
      )

      onDispose {
        Log.d("TabMainScreen", "Unbinding service")
        ctx.unbindService(conn)
      }
    }

    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      LazyColumn {
        items(pendingList) { picSectionData ->
          Box(
            modifier = Modifier.fillMaxWidth(),
          ) {
            var counter: Int? = progressMap[picSectionData.picSectionBean.id]
            var totalCount = picSectionData.totalCount
            if (totalCount == 0) {
              counter = 0
              totalCount = 1
            }
            if (counter == null) {
              counter = 0
            }
            if (totalCount <= counter) {
              counter = totalCount
            }
            PendingItem(
              text = picSectionData.picSectionBean.name,
              process = counter,
              total = totalCount
            )
          }
        }
      }
    }
  }
}

class AlbumConfig(
  val id: Long,
  val name: String,
  val sourcePath: String,
  val coverSection: Flow1000Section,
)

class Flow1000Section(
  val id: Long,
  val name: String,
  val dirName: String,
  val cover: String,
  val album: String,
  val coverWidth: Int,
  val coverHeight: Int,
)