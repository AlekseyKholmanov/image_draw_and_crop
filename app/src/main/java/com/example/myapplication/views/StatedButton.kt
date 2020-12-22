package com.example.myapplication.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class StatedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ImageButton(context, attrs, defStyle) {

    var isPaused: Boolean = false
        set(value) {
            field = value
            updateState()
        }

    private val image =
        ContextCompat.getDrawable(context, R.drawable.ic_play_shaped) as LayerDrawable
    private val playDrawable = ContextCompat.getDrawable(context, R.drawable.ic_play)
    private val stopDrawable = ContextCompat.getDrawable(context, R.drawable.ic_pause)


    init {
        setBackgroundColor(Color.TRANSPARENT)
        setImageDrawable(image)
    }

    private fun updateState() {
        image.mutate()
        val updatedIcon = if(isPaused) playDrawable else stopDrawable
        image.setDrawableByLayerId(R.id.reuseIcon, updatedIcon)
        invalidate()
    }


}