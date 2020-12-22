package com.example.myapplication.adapters.fragment

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.myapplication.EditImageActivity
import com.example.myapplication.R
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.item_gallery_image.*

class ImageFragment : Fragment(R.layout.item_gallery_image) {
    companion object {
        const val IMAGE_RES_KEY = "IMAGE_RES_KEY"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageRes = arguments?.getInt(IMAGE_RES_KEY) ?: return
        image.setImageResource(imageRes)
        image.apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setOnTouchListener { v, event ->
                var result = true
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if (event.pointerCount >= 2 || v.canScrollHorizontally(1)
                    && canScrollHorizontally(-1)
                ) {
                    //multi-touch event
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Disable touch on view
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> {
                            true
                        }
                    }
                }
                //block viewPager swipe if image zoomed
                if ((v as TouchImageView).isZoomed) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    false
                } else {
                    result
                }
            }
        }
        edit.setOnClickListener {
            val intent = Intent(requireContext(), EditImageActivity::class.java).apply {
                putExtra(IMAGE_RES_KEY, imageRes)
            }
            startActivity(intent)
        }
    }

}