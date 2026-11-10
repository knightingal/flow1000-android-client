package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
fun PendingItem(text: String, process: Int, total: Int) {
    FlexBox(Modifier.fillMaxWidth().padding(8.dp), config = {
        gap(8.dp)
    }) {
        Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.flex {
            shrink(1f)
        })
        Text(text = "$process/$total", modifier = Modifier.flex {
            shrink(0f)
        })
    }
}

@Composable
@Preview(showBackground = true)
fun PendingItemPreview() {
    Flow1000clientTheme {
        PendingItem("Sample Text, Sample Text, Sample Text, Sample Text", 5, 10)
    }
}