package com.example.myapplication.adapters.items

import com.example.myapplication.R

class ImageItem(
    val imageResId: Int
) : RecyclerItem {
    companion object {

        const val VIEW_TYPE = R.layout.item_gallery_image
    }

    override val viewType: Int = VIEW_TYPE
}

