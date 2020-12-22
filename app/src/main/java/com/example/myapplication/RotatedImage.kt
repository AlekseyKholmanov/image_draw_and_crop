package com.example.myapplication

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load

class RotatedImage @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {
    private val leftRotate: Button
    private val rightRotate: Button
    private val image: ImageView

    var degree = 0f


    init {
        val view = View.inflate(context, R.layout.merge_rotated_image, this)
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        image = view.findViewById(R.id.rotatedImage)
        leftRotate = view.findViewById(R.id.rotateLeft)
        rightRotate = view.findViewById(R.id.rotateRight)

        rightRotate.setOnClickListener {
            degree += 90
            rotate()
        }
        leftRotate.setOnClickListener {
            degree -= 90
            rotate()
        }

    }

    fun setImage(imageResId: Int) {
        image.load(imageResId)
    }

    private fun rotate() {
        image.rotation = degree
    }

}