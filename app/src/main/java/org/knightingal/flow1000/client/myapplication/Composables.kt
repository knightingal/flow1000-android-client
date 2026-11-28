package org.knightingal.flow1000.client.myapplication

import android.os.Environment
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlexAlignSelf
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.R
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme
import org.knightingal.flow1000.client.util.EnvArgs.Companion.PROTOCOL_PREFIX
import java.io.File
import kotlin.math.roundToInt

interface CardCover<T> {
  val width: Int
  val height: Int
  val coverUri: T
  val name: String
}

@Composable
fun AlbumCoverCard(
  albumCover: CardCover<*>,
  sharedTransitionScope: SharedTransitionScope? = null,
  animatedContentScope: AnimatedContentScope? = null,
  appendContent: @Composable () -> Unit = {},
  edit: Boolean = false,
  sharedKey: String? = null,
  onClick: () -> Unit = {},
) {
  val modifier = if (sharedTransitionScope != null && sharedKey != null) {
    with(sharedTransitionScope) {
      Modifier.sharedBounds(
        sharedContentState = sharedTransitionScope.rememberSharedContentState(sharedKey),
        animatedVisibilityScope = animatedContentScope!!,
        enter = fadeIn(),
        exit = fadeOut(),
      )
    }
  } else {
    Modifier
  }

  Card(
    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
    colors = CardColors(
      contentColor = MaterialTheme.colorScheme.onSurface,
      containerColor = MaterialTheme.colorScheme.surface,
      disabledContainerColor = Color.LightGray,
      disabledContentColor = Color.LightGray,
    ), modifier = modifier
      .fillMaxWidth()
      .wrapContentSize(),
    onClick = onClick
  ) {
    ImageContentInCard(albumCover)
    if (edit) {
      OutlinedTextField(
        readOnly = true,
        colors = TextFieldDefaults.colors(
          errorIndicatorColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          disabledIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
        value = albumCover.name,
        onValueChange = {}
      )
    } else {
      Text(albumCover.name, modifier = Modifier.padding(16.dp))
    }
    appendContent()
  }
}

@Composable
fun ImageContentInCard(
  cardCover: CardCover<*>,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(
        ratio = cardCover.width.toFloat()
            / cardCover.height.toFloat()
      )
  ) {
    if (cardCover.coverUri is Int) {
      Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(cardCover.coverUri as Int),
        contentDescription = null,
      )
    } else if (cardCover.coverUri is String) {
      val coverUri = cardCover.coverUri as String
      if (coverUri.startsWith("${PROTOCOL_PREFIX}//")) {
        AsyncImage(
          modifier = Modifier.fillMaxSize(),
          model = coverUri,
          contentDescription = null,
        )

      } else {
        val imageFile = File(
          LocalContext.current.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
          coverUri
        )
        AsyncImage(
          modifier = Modifier.fillMaxSize(),
          model = imageFile,
          contentDescription = null,
        )
      }
    }
  }
}

@Composable
@Preview
fun AlbumCoverCardEditPreview() {
  Flow1000clientTheme {
    AlbumCoverCard(
      albumCover = object : CardCover<Int> {
        override val width: Int
          get() = 640
        override val height: Int
          get() = 426
        override val coverUri: Int
          get() = R.drawable.fc6_nightly_wind_down
        override val name: String
          get() = "EditAI20220605211354"
      },
      edit = true
    )
  }
}

@Serializable
class SectionCover(
  override val name: String,
  val coverWidth: Int,
  val coverHeight: Int,
  val cover: String,
) : CardCover<String> {
  override val width: Int = coverWidth
  override val height: Int = coverHeight
  override val coverUri: String = "/${name}/${cover}"
}

@Serializable
class ExistAlbumCover(
  override val name: String,
  val coverWidth: Int,
  val coverHeight: Int,
  val cover: String,
) : CardCover<String> {
  override val width: Int = coverWidth
  override val height: Int = coverHeight
//  override val coverUri: String = "/${name}/${cover}"
  override val coverUri: String
    get() = cover
}

class OnlineAlbumCover(
  override val name: String,
  coverWidth: Int,
  coverHeight: Int,
  override val coverUri: String,
) : CardCover<String> {
  override val width: Int = coverWidth
  override val height: Int = coverHeight
}

@Composable
fun PendingItem(text: String, process: Int, total: Int) {
  FlexBox(Modifier.fillMaxWidth().padding(8.dp), config = {
    gap(8.dp)
  }) {
    Text(
      text = text,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.flex {
        shrink(1f)
        grow(1f)
        alignSelf (FlexAlignSelf.Center)
      }
    )
    CircularProgressIndicator(
      progress = {process.toFloat() / total.toFloat()} ,
      modifier = Modifier.flex {
        grow(0f)
        shrink(0f)
      }.size(24.dp)
    )
  }
}

@ExperimentalMaterial3Api
@Composable
fun PrimaryTabRowExt(
  selectedTabIndex: Int,
  pageOffsetFraction: Float = 0f,
  modifier: Modifier = Modifier,
  containerColor: Color = TabRowDefaults.primaryContainerColor,
  contentColor: Color = TabRowDefaults.primaryContentColor,
  indicator: @Composable TabIndicatorScopeExt.() -> Unit = {
    TabRowDefaults.PrimaryIndicator(
      modifier = Modifier.tabIndicatorOffset(selectedTabIndex, pageOffsetFraction, matchContentSize = true),
      width = Dp.Unspecified,
    )
  },
  divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
  tabs: @Composable () -> Unit
) {
  TabRowImplExt(modifier, containerColor, contentColor, indicator, divider, tabs)
}

internal interface TabPositionsHolder {
  fun setTabPositions(positions: List<TabPositionExt>)
}

internal class TabIndicatorOffsetNode(
  var tabPositionsState: State<List<TabPositionExt>>,
  var selectedTabIndex: Int,
  var pageOffsetFraction: Float,
  var followContentSize: Boolean
) : Modifier.Node(), LayoutModifierNode {

  override fun MeasureScope.measure(
    measurable: Measurable,
    constraints: Constraints
  ): MeasureResult {
    if (tabPositionsState.value.isEmpty()) {
      return layout(0, 0) {}
    }
    Log.d("TabIndicatorOffsetNode", "selectedTabIndex: $selectedTabIndex, pageOffsetFraction: $pageOffsetFraction")
    val progress: Float = selectedTabIndex + pageOffsetFraction
    val low = progress.toInt()
    var high = low + 1
    if (high >= tabPositionsState.value.size) {
      high = low
    }
    val percentage: Float = progress - low

    val lowWidth = tabPositionsState.value[low].contentWidth
    val highWidth = tabPositionsState.value[high].contentWidth
    val indicatorWidth = lowWidth + ((highWidth - lowWidth) * percentage)

    val lowOffset = tabPositionsState.value[low].left
    val highOffset = tabPositionsState.value[high].left
    val indicatorOffset = lowOffset + ((highOffset - lowOffset) * percentage)

    val offset: Dp = indicatorOffset
    val width: Dp = indicatorWidth

    val placeable =
      measurable.measure(
        constraints.copy(minWidth = width.roundToPx(), maxWidth = width.roundToPx())
      )

    val result = layout(
      width = placeable.width,
      height = placeable.height,
      placementBlock = {
        placeable.place(offset.roundToPx(), 0)
      }
    )
    return result
  }
}

internal data class TabIndicatorModifier(
  val tabPositionsState: State<List<TabPositionExt>>,
  val selectedTabIndex: Int,
  val pageOffsetFraction: Float,
  val followContentSize: Boolean,
) : ModifierNodeElement<TabIndicatorOffsetNode>() {

  override fun create(): TabIndicatorOffsetNode {
    return TabIndicatorOffsetNode(
      tabPositionsState = tabPositionsState,
      selectedTabIndex = selectedTabIndex,
      pageOffsetFraction = pageOffsetFraction,
      followContentSize = followContentSize,
    )
  }

  override fun update(node: TabIndicatorOffsetNode) {
    node.tabPositionsState = tabPositionsState
    node.selectedTabIndex = selectedTabIndex
    node.pageOffsetFraction = pageOffsetFraction
    node.followContentSize = followContentSize
  }

  override fun InspectorInfo.inspectableProperties() {
    // Show nothing in the inspector.
  }
}

@Immutable
class TabPositionExt internal constructor(val left: Dp, val width: Dp, val contentWidth: Dp) {

  val right: Dp
    get() = left + width

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TabPositionExt) return false

    if (left != other.left) return false
    if (width != other.width) return false
    if (contentWidth != other.contentWidth) return false

    return true
  }

  override fun hashCode(): Int {
    var result = left.hashCode()
    result = 31 * result + width.hashCode()
    result = 31 * result + contentWidth.hashCode()
    return result
  }

  override fun toString(): String {
    return "TabPositionExt(left=$left, right=$right, width=$width, contentWidth=$contentWidth)"
  }
}

@ExperimentalMaterial3Api
interface TabIndicatorScopeExt {

  /**
   * A layout modifier that provides tab positions, this can be used to animate and layout a
   * TabIndicator depending on size, position, and content size of each Tab.
   *
   * @sample androidx.compose.material3.samples.FancyAnimatedIndicatorWithModifier
   */
  fun Modifier.tabIndicatorLayout(
    measure:
    MeasureScope.(
      Measurable,
      Constraints,
      List<TabPositionExt>,
    ) -> MeasureResult
  ): Modifier

  /**
   * A Modifier that follows the default offset and animation
   *
   * @param selectedTabIndex the index of the current selected tab
   * @param matchContentSize this modifier can also animate the width of the indicator \ to match
   *   the content size of the tab.
   */
  fun Modifier.tabIndicatorOffset(
    selectedTabIndex: Int,
    pageOffsetFraction: Float,
    matchContentSize: Boolean = false
  ): Modifier
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabRowImplExt(
  modifier: Modifier,
  containerColor: Color,
  contentColor: Color,
  indicator: @Composable TabIndicatorScopeExt.() -> Unit,
  divider: @Composable () -> Unit,
  tabs: @Composable () -> Unit
) {
  Surface(
    modifier = modifier.selectableGroup(),
    color = containerColor,
    contentColor = contentColor
  ) {
    val scope = remember {
      object : TabIndicatorScopeExt, TabPositionsHolder {

        val tabPositions = mutableStateOf<(List<TabPositionExt>)>(listOf())

        override fun Modifier.tabIndicatorLayout(
          measure:
          MeasureScope.(
            Measurable,
            Constraints,
            List<TabPositionExt>,
          ) -> MeasureResult
        ): Modifier =
          this.layout { measurable: Measurable, constraints: Constraints ->
            measure(
              measurable,
              constraints,
              tabPositions.value,
            )
          }

        override fun Modifier.tabIndicatorOffset(
          selectedTabIndex: Int,
          pageOffsetFraction: Float,
          matchContentSize: Boolean
        ): Modifier =
          this.then(
            TabIndicatorModifier(tabPositions, selectedTabIndex, pageOffsetFraction, matchContentSize)
          )

        override fun setTabPositions(positions: List<TabPositionExt>) {
          tabPositions.value = positions
        }
      }
    }

    Layout(
      modifier = Modifier.fillMaxWidth(),
      contents =
        listOf(
          tabs,
          divider,
          { scope.indicator() },
        )
    ) { (tabMeasurables, dividerMeasurables, indicatorMeasurables), constraints ->
      val tabRowWidth = constraints.maxWidth
      val tabCount = tabMeasurables.size
      var tabWidth = 0
      if (tabCount > 0) {
        tabWidth = (tabRowWidth / tabCount)
      }
      val tabRowHeight =
        tabMeasurables.fastFold(initial = 0) { max, curr ->
          maxOf(curr.maxIntrinsicHeight(tabWidth), max)
        }

      scope.setTabPositions(
        List(tabCount) { index ->
          var contentWidth =
            minOf(tabMeasurables[index].maxIntrinsicWidth(tabRowHeight), tabWidth)
              .toDp()
          contentWidth -= HorizontalTextPadding * 2
          // Enforce minimum touch target of 24.dp
          val indicatorWidth = maxOf(contentWidth, 24.dp)

          TabPositionExt(tabWidth.toDp() * index, tabWidth.toDp(), indicatorWidth)
        }
      )

      val tabPlaceables =
        tabMeasurables.fastMap {
          it.measure(
            constraints.copy(
              minWidth = tabWidth,
              maxWidth = tabWidth,
              minHeight = tabRowHeight,
              maxHeight = tabRowHeight,
            )
          )
        }

      val dividerPlaceables =
        dividerMeasurables.fastMap { it.measure(constraints.copy(minHeight = 0)) }

      val indicatorPlaceables =
        indicatorMeasurables.fastMap {
          it.measure(
            constraints.copy(
              minWidth = tabWidth,
              maxWidth = tabWidth,
              minHeight = 0,
              maxHeight = tabRowHeight
            )
          )
        }

      layout(tabRowWidth, tabRowHeight) {
        tabPlaceables.fastForEachIndexed { index, placeable ->
          placeable.placeRelative(index * tabWidth, 0)
        }

        dividerPlaceables.fastForEach { placeable ->
          placeable.placeRelative(0, tabRowHeight - placeable.height)
        }

        indicatorPlaceables.fastForEach { it.placeRelative(0, tabRowHeight - it.height) }
      }
    }
  }
}

internal val HorizontalTextPadding = 16.dp
