package com.example.livestreamexoplayer


import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.livestreamexoplayer.ui.theme.LiveStreamExoPlayerTheme


class MainActivity : ComponentActivity() {

    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LiveStreamExoPlayerTheme {
                // A surface container
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Your Jetpack Compose UI code here
                    val playerView = rememberPlayerView()

                    // Initialize ExoPlayer
                    initializePlayer(playerView)

                    // Use playerView in your UI
                    PlayerViewWrapper(playerView)
                }
            }
        }

//         Add lifecycle observer to release ExoPlayer when the app is in the background
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onBackground() {
                exoPlayer?.playWhenReady = false
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onForeground() {
                exoPlayer?.playWhenReady = true
            }
        })
    }


    // We can use this as well
//    override fun onPause() {
//        super.onPause()
//        exoPlayer?.playWhenReady = false
//    }
//
//    override fun onResume() {
//        super.onResume()
//        exoPlayer?.playWhenReady = true
//    }


    @Composable
    private fun rememberPlayerView(): PlayerView {
        return remember {
            PlayerView(this@MainActivity).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    private fun initializePlayer(playerView: PlayerView) {


        exoPlayer = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                // Update the track selection parameters to only pick standard definition tracks
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()
                exoPlayer.setMediaItems(listOf(mediaItem))

                exoPlayer.prepare()
            }



        // For normal video media_url_mp4
//        exoPlayer = ExoPlayer.Builder(this).build()
//        playerView.player = exoPlayer
//        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
//        exoPlayer!!.setMediaItem(mediaItem)
//        exoPlayer!!.prepare()
//        exoPlayer!!.play()


    }


}

// Helper function to create a PlayerViewWrapper
@Composable
fun PlayerViewWrapper(playerView: PlayerView) {
    CompositionLocalProvider(LocalDensity provides LocalDensity.current) {
        AndroidView(
            factory = { playerView },
            update = { view ->

            }
        )
    }
}



