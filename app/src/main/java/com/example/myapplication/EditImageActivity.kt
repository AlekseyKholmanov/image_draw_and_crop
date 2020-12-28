package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import coil.load
import com.example.myapplication.dialogs.BrushPropertyListener
import com.example.myapplication.dialogs.BrushSettingsBottomDialog
import com.example.myapplication.dialogs.TextEditorCallback
import com.example.myapplication.dialogs.TextEditorDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import kotlinx.android.synthetic.main.activity_edit_image.*
import java.io.File


class EditImageActivity : AppCompatActivity(R.layout.activity_edit_image) {

    lateinit var photoEditor: PhotoEditor

    companion object {
        const val URI = "URI"
    }


    var color: Int = 0
        set(value) {
            field = value
            photoEditor.brushColor = value
        }
    var lastColorPosition:Float = 0f
    var saveRect: Rect? = null
    var rotation: Int? = null

    private val brushPropertyListener = object : BrushPropertyListener {
        override fun onColorChanged(colorCode: Int, lastThumbPosition: Float) {
            color = colorCode
            this@EditImageActivity.lastColorPosition = lastThumbPosition
        }

        override fun onOpacityChanged(opacity: Int) {
            photoEditor.setOpacity(opacity)
        }

        override fun onBrushSizeChanged(brushSize: Int) {
            photoEditor.brushSize = brushSize.toFloat()
        }

    }

    val photoEditorListener = object: OnPhotoEditorListener{

        override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
            val fragment = TextEditorDialog.showInstance(this@EditImageActivity, text, colorCode)
            fragment.setCallback(object : TextEditorCallback {
                override fun onDone(newText: String, colorCode: Int) {
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    photoEditor.editText(rootView, newText, styleBuilder)
                }
            })
        }

        override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        }

        override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        }

        override fun onStartViewChangeListener(viewType: ViewType?) {
        }

        override fun onStopViewChangeListener(viewType: ViewType?) {
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val imageRes = intent.extras?.getInt(ImageFragment.IMAGE_RES_KEY) ?: return
        val uri = intent.extras?.getString(URI)
        Log.d("M_M_","incomeUri: ${uri.toString()}")
        image.source.load(uri)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(uri)), null, options)


        Log.d("M_M_", "initial: $uri w: ${options.outWidth} h:${options.outHeight}")
        initToolbar()
        photoEditor = PhotoEditor.Builder(this, image)
            .setPinchTextScalable(true)
            .build()

        photoEditor.setOnPhotoEditorListener(photoEditorListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_editor, menu)
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit image"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.text -> {
                val fragment = TextEditorDialog.showInstance(this, "", color)
                fragment.setCallback(object : TextEditorCallback {
                    override fun onDone(newText: String, colorCode: Int) {
                        val styleBuilder = TextStyleBuilder()
                        styleBuilder.withTextColor(colorCode)
                        photoEditor.addText(newText, styleBuilder)
                    }
                })
                true
            }
            R.id.undo -> {
                photoEditor.undo()
            }
            R.id.brush -> {
                photoEditor.setBrushDrawingMode(true)
                openSettingsDialog()
                true
            }
            R.id.crop -> {
                val act = CropImage.activity(Uri.parse(intent.extras?.getString(URI).toString()))
                    .setGuidelines(CropImageView.Guidelines.ON)
                saveRect?.let {
                    act.setInitialCropWindowRectangle(it)
                }
                rotation?.let {
                    act.setInitialRotation(it)
                }
                act.start(this)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val uri = result.uri
            val options = BitmapFactory.Options()
            val croppedRect = result.cropRect
            Log.d("E_M_EditImageActivity", "saved: t:${saveRect?.top} l: ${saveRect?.left} b: ${saveRect?.bottom} r:${saveRect?.right}")
            Log.d("E_M_EditImageActivity", "wholeImage: t:${result.wholeImageRect?.top} l: ${result.wholeImageRect?.left} b: ${result.wholeImageRect?.bottom} r:${result.wholeImageRect?.right}")
            val newRect = Rect().apply {
                left = result.wholeImageRect.bottom - croppedRect!!.bottom
                top = croppedRect.left
                right = result.wholeImageRect.bottom - croppedRect.top
                bottom = croppedRect.right
            }
            val yScale = result.wholeImageRect.right.toFloat() / (newRect.bottom - newRect.top)
            val xScale = result.wholeImageRect.bottom.toFloat() / (newRect.right - newRect.left)
            val canvasXOffset = newRect.left.toFloat() / result.wholeImageRect.bottom

            val canvasYOffset = newRect.top.toFloat() / result.wholeImageRect.right
            Log.d("E_M_EditImageActivity", "x: ${newRect.left} y: ${newRect.top} xScale: $xScale yScale: $yScale canvasXOffset: $canvasXOffset canvasYOffset: $canvasYOffset")
            photoEditor.setDrawableOffset(newRect.left, newRect.top, xScale, yScale, canvasXOffset, canvasYOffset)
            saveRect = newRect
            rotation = result.rotation

            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(uri.path).absolutePath, options)
            image.source.setImageURI(uri)
            Log.d("M_M_", "after crop: $uri w: ${options.outWidth} h:${options.outHeight}")
        }
    }

    private fun openSettingsDialog() {
        val fragment = BrushSettingsBottomDialog()
        val currentSettings = bundleOf(
            "lastColorPosition" to lastColorPosition,
            "brush" to photoEditor.brushSize
        )
        fragment.arguments = currentSettings
        fragment.setCallback(brushPropertyListener)
        supportFragmentManager.beginTransaction()
            .add(fragment, BrushSettingsBottomDialog.TAG)
            .commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}