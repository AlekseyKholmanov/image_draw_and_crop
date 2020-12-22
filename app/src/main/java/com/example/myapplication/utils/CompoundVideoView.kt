package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController
import com.example.myapplication.R
import com.example.myapplication.views.StatedButton

class CompoundVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    val button: StatedButton
    val video: CustomVideoView
    val mediaController: MediaController

    init {
        val view = inflate(context, R.layout.compound_video_view, this)

        video = view.findViewById(R.id.videoView)
        button = view.findViewById(R.id.controlButton)
        mediaController = MediaController(context)


        mediaController.setAnchorView(video)
        video.setMediaController(mediaController)
        video.setOnCompletionListener {
            button.isPaused = true
            button.visibility = View.VISIBLE
            mediaController.show(0)
        }
        video.setPlayPauseListener(object : VideoViewListener {
            override fun onPause() {
                Log.d("M_CompoundVideoView", "is paused")
                button.isPaused = true
                mediaController.show(0)
                button.visibility = View.VISIBLE
            }

            override fun onPlay() {
                button.isPaused = false
                button.visibility = View.GONE
                mediaController.hide()
            }

            override fun onTouch() {
                Log.d("M_CompoundVideoView", "touched")
                if (button.visibility == View.VISIBLE) {

                    button.visibility = View.GONE
                    mediaController.hide()
                } else {
                    button.visibility = View.VISIBLE
                    mediaController.show(0)
                }

            }
        })

        button.setOnClickListener {
            if (video.isPlaying) {
                video.pause()
            } else {
                video.start()
            }
        }
    }

    fun setUri(uri: Uri) {
        video.setVideoURI(uri)
        video.seekTo(1)
    }
}