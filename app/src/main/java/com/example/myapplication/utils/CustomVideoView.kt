package com.example.myapplication.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.VideoView

class CustomVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : VideoView(context, attrs, defStyle) {


    var listener: VideoViewListener? = null

    fun setPlayPauseListener(listener: VideoViewListener?){
        this.listener = listener
    }

    override fun pause() {
        super.pause()
        listener?.onPause()
    }

    override fun start() {
        super.start()
        listener?.onPlay()
    }



    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if( ev?.action == MotionEvent.ACTION_DOWN){
            listener?.onTouch()
             false
        } else super.onTouchEvent(ev)
    }
}


interface VideoViewListener{
    fun onPause()
    fun onPlay()
    fun onTouch()
}