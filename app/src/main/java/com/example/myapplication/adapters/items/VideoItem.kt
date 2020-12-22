package com.example.myapplication.adapters.items

import com.example.myapplication.R

class VideoItem(
    val uri: String
): RecyclerItem {


    companion object{
        const val VIEW_TYPE = R.layout.item_gallery_video
    }
    override val viewType: Int = VIEW_TYPE

}