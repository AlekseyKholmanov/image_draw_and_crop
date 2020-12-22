package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapters.ViewPagerAdapter
import com.example.myapplication.adapters.items.ImageItem
import com.example.myapplication.adapters.items.RecyclerItem
import com.example.myapplication.adapters.items.VideoItem
import kotlinx.android.synthetic.main.activity_pager.*
import java.util.*

class PagerActivity : AppCompatActivity() {


    private val items =
        arrayOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
        )

    private var recyclerItems = mutableListOf<RecyclerItem>()


    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        ViewPagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = intent.extras?.getInt("position")
        setContentView(R.layout.activity_pager)
        initToolbar()
        initAdapter()
        position?.let {
            viewPager.setCurrentItem(it, false)
        }
        //val pageTransformer =
        //    if (Build.VERSION.SDK_INT >= 21) DepthPageTransformer() else ZoomOutPageTransformer()
        //viewPager.setPageTransformer(pageTransformer)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initAdapter() {
        adapter.setItems(getItems())
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                supportActionBar?.title = recyclerItems[position].viewType.toString()
                supportActionBar?.subtitle = Date().time.toString()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getItems(): List<RecyclerItem> {

        val videoUri1 = "android.resource://" + packageName + "/" + R.raw.video
        val videoUri2 = "android.resource://" + packageName + "/" + R.raw.video2
        val videoUri3 = "android.resource://" + packageName + "/" + R.raw.video3
        recyclerItems.addAll(this.items.map {
            ImageItem(it)
        })
        recyclerItems.add(VideoItem(videoUri1))
        recyclerItems.add(VideoItem(videoUri2))
        recyclerItems.add(VideoItem(videoUri3))
        return recyclerItems
    }
}