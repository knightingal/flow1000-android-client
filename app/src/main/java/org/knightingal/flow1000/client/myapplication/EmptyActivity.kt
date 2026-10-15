package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import coil3.compose.AsyncImage
import org.knightingal.flow1000.client.R
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme
import java.io.File

class EmptyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Flow1000clientTheme {
                Greeting("EmptyActivity")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Flow1000clientTheme {
        Greeting("Android")
    }
}

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
    appendContent: @Composable () -> Unit = {},
    edit: Boolean = false,
    onClick: () -> Unit = {},
) {
    val modifier = if (sharedTransitionScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = sharedTransitionScope.rememberSharedContentState(albumCover.coverUri as String),
                animatedVisibilityScope = animatedContentScope!!
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
            .fillMaxWidth().wrapContentSize(),
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
        modifier = Modifier.fillMaxWidth().aspectRatio(
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
            val imageFile = File(
                LocalContext.current.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                cardCover.coverUri as String
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
