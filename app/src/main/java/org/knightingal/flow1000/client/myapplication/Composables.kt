package org.knightingal.flow1000.client.myapplication

import android.os.Environment
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.github.awxkee.avifcoil.decoder.HeifDecoder
import kotlinx.serialization.Serializable
import org.knightingal.flow1000.client.R
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme
import java.io.File

interface CardCover<T> {
  val width: Int
  val height: Int
  val coverUri: T
  val name: String
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumCoverCard(
  albumCover: CardCover<*>,
  sharedTransitionScope: SharedTransitionScope? = null,
  animatedContentScope: AnimatedContentScope? = null,
  appendContent: () -> Unit = {},
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

@OptIn(ExperimentalSharedTransitionApi::class)
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
class ExistAlbumCover(
  override val name: String,
  val coverWidth: Int,
  val coverHeight: Int,
  val cover: String,
) : CardCover<String> {
  override val width: Int = coverWidth
  override val height: Int = coverHeight
  override val coverUri: String = "/${name}/${cover}"
}
