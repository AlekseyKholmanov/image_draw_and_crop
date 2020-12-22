package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.adapters.fragment.ImageFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val PICK_IMAGE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        open1.setOnClickListener {
            sendIntent(1)
        }
        open3.setOnClickListener {
            sendIntent(3)
        }
        open5.setOnClickListener {
            sendIntent(5)
        }
        withoutMove.setOnClickListener {
            sendIntent()
        }
        pickFile.setOnClickListener {
            sendGalleryIntent()
        }
    }

    private fun sendGalleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE){
            val uri = data?.data ?: return
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra(EditImageActivity.URI , uri.toString())
            startActivity(intent)
        }
    }

    private fun sendIntent(position: Int? = null) {
        val intent = Intent(this, PagerActivity::class.java)
        if (position != null) {
            intent.putExtra("position", position)
        }
        startActivity(intent)
    }

}
