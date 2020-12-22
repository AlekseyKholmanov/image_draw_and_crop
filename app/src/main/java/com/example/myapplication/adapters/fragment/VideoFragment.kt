package com.example.myapplication.adapters.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.utils.VideoViewListener
import kotlinx.android.synthetic.main.item_gallery_video.*

class VideoFragment : Fragment(R.layout.item_gallery_video) {

    companion object {

        const val VIDEO_URI_KEY = "VideoURIKey"
    }

    private val mediaController: MediaController by lazy(LazyThreadSafetyMode.NONE) {
        MediaController(requireContext())
    }

    private var lastPosition = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            "M_VideoFragment",
            "${this.hashCode()} on create ${arguments?.getString(VIDEO_URI_KEY)} position: ${arguments?.getInt(
                "last_position" 
            )} instant state $savedInstanceState"
        )
        if (savedInstanceState != null) {
            lastPosition = arguments?.getInt("last_position") ?: 0
        }
        val uri = arguments?.getString(VIDEO_URI_KEY) ?: return

        video.apply {
            setVideoURI(Uri.parse(uri))
            setMediaController(mediaController)
            if (lastPosition > 0) seekTo(lastPosition) else seekTo(1)
            requestFocus()

            setOnCompletionListener {
                controlButton.visibility = View.VISIBLE
                mediaController.show(0)
                controlButton.isPaused = true
            }
        }

        mediaController.setAnchorView(video)


        controlButton.setOnClickListener {
            if (video.isPlaying) {
                video.pause()
            } else {
                video.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_VideoFragment" ,"${this.hashCode()} on start" )
    }


    override fun onResume() {
        super.onResume()
        Log.d(
            "M_VideoFragment",
            "${this.hashCode()}on resume position: ${arguments?.getInt("last_position")} \ncontroller${mediaController.hashCode()}"
        )
        video.setPlayPauseListener(object : VideoViewListener{
            override fun onPause() {
                    controlButton.isPaused = true
                    mediaController.show(0)
                    controlButton.visibility = View.VISIBLE
                }

                override fun onPlay() {
                    controlButton.isPaused = false
                    controlButton.visibility = View.GONE
                    mediaController.hide()
                }

                override fun onTouch() {
                    if (controlButton.visibility == View.VISIBLE) {

                        controlButton.visibility = View.GONE
                        mediaController.hide()
                    } else {
                        controlButton.visibility = View.VISIBLE
                        mediaController.show(0)
                    }
                }
            })
        if (lastPosition != 1 && video.isPlaying.not()){
            video.seekTo(lastPosition)
        }
        if(video.isPlaying.not()){
            controlButton.visibility = View.VISIBLE

        }
    }

    override fun onPause() {
        super.onPause()
        video.setPlayPauseListener(null)
        Log.d("M_VideoFragment", "${this.hashCode()} pause last pos: ${video.currentPosition}")
        mediaController.hide()
        lastPosition = video.currentPosition
    }

    override fun onStop() {
        Log.d("M_VideoFragment","${this.hashCode()} stop")
        video.stopPlayback()
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("M_VideoFragment","${this.hashCode()} destroy")
        super.onDestroyView()
        video.setMediaController(null)
        arguments?.putInt("last_position", lastPosition)
    }
}