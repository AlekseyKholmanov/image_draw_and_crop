package com.example.myapplication.adapters

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.adapters.fragment.ImageFragment
import com.example.myapplication.adapters.fragment.VideoFragment
import com.example.myapplication.adapters.items.ImageItem
import com.example.myapplication.adapters.items.RecyclerItem
import com.example.myapplication.adapters.items.VideoItem
import java.lang.IllegalStateException

class ViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa ) {
    private var items = mutableListOf<RecyclerItem>()

    override fun createFragment(position: Int): Fragment {
        Log.d("M_ViewPagerAdapter","create fragment $position")
        val item = items[position]
        return when(item){
            is ImageItem -> {
                val bundle = bundleOf(ImageFragment.IMAGE_RES_KEY to item.imageResId)
                val fragment = ImageFragment()
                fragment.arguments = bundle
                fragment
            }
            is VideoItem -> {
                val bundle = bundleOf(VideoFragment.VIDEO_URI_KEY to item.uri)
                val fragment = VideoFragment()
                fragment.arguments = bundle
                fragment
            }
            else -> {

                throw IllegalStateException("wrong item view type")
            }
        }
    }



    override fun getItemCount(): Int = items.count()

    fun setItems(newItems: List<RecyclerItem>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}