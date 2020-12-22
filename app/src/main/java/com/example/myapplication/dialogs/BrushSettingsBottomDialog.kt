package com.example.myapplication.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.myapplication.R
import com.example.myapplication.views.ColorSeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_text_settings.*

class BrushSettingsBottomDialog : BottomSheetDialogFragment() {

    companion object {
        val TAG = BrushSettingsBottomDialog::class.simpleName
    }

    private var _brushPropertyListener: BrushPropertyListener? = null

    private val seekBarCallback = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            when (seekBar?.id) {
                R.id.brushSeek -> {
                    _brushPropertyListener?.onBrushSizeChanged(progress)
                }

                R.id.opacitySeek -> {
                    _brushPropertyListener?.onOpacityChanged(progress)
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    }

    private val colorChangeCallback by lazy {
        object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(color: Int) {
                _brushPropertyListener?.onColorChanged(color, colorSeek.thumbX)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottom_text_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        brushSeek.setOnSeekBarChangeListener(seekBarCallback)
        opacitySeek.setOnSeekBarChangeListener(seekBarCallback)
        colorSeek.setOnColorChangeListener(colorChangeCallback)
        val color = arguments?.getFloat("lastColorPosition", 0f)
        val brush = arguments?.getFloat("brush", 25f)
        color?.let {
            colorSeek.setLastThumbPosition(it)
        }
        brush?.let {
            brushSeek.progress = it.toInt()
        }

    }

    fun setCallback(brushPropertyListener: BrushPropertyListener) {
        _brushPropertyListener = brushPropertyListener
    }
}


interface BrushPropertyListener {
    fun onColorChanged(colorCode: Int, lastThumbPosition: Float)

    fun onOpacityChanged(opacity: Int)

    fun onBrushSizeChanged(brushSize: Int)
}