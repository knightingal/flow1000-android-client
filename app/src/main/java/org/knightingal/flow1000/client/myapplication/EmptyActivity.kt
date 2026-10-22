package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.knightingal.flow1000.client.myapplication.ui.theme.Flow1000clientTheme

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



@Composable
fun AutoAdvancePager(pageItems: List<Color>, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { pageItems.size })
        val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

        val pageInteractionSource = remember { MutableInteractionSource() }
        val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

        // Stop auto-advancing when pager is dragged or one of the pages is pressed
        val autoAdvance = !pagerIsDragged && !pageIsPressed

//        if (autoAdvance) {
//            LaunchedEffect(pagerState, pageInteractionSource) {
//                while (true) {
//                    delay(2000.milliseconds)
//                    val nextPage = (pagerState.currentPage + 1) % pageItems.size
//                    pagerState.animateScrollToPage(nextPage)
//                }
//            }
//        }

        HorizontalPager(
            state = pagerState
        ) { page ->
            Text(
                text = "Page: $page",
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxSize()
                    .background(pageItems[page])
                    .clickable(
                        interactionSource = pageInteractionSource,
                        indication = LocalIndication.current
                    ) {
                        // Handle page click
                    }
                    .wrapContentSize(align = Alignment.Center)
            )
        }

        PagerIndicator(pageItems.size, pagerState.currentPage)
    }
}


@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (currentPageIndex == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AutoAdvancePagerPreview() {
    val pageItems: List<Color> = listOf(
        Color.Red,
        Color.Gray,
        Color.Green,
        Color.White
    )
    AutoAdvancePager(pageItems = pageItems)
}