package org.knightingal.flow1000.client.myapplication

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import org.knightingal.flow1000.client.databinding.ActivityMainBinding
import org.knightingal.flow1000.client.databinding.ActivityStableDiffusionRepoBinding

class StableDiffusionRepoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStableDiffusionRepoBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityStableDiffusionRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webview.loadUrl("http://192.168.2.212:8000")
    }
}