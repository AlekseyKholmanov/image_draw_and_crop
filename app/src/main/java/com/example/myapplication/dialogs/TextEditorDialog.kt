package com.example.myapplication.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.views.ColorSeekBar
import kotlinx.android.synthetic.main.dialog_text_editor.*

class TextEditorDialog : DialogFragment() {

    companion object {

        val EXTRA_INPUT_TEXT = "extra_input_text"
        val EXTRA_COLOR_CODE = "extra_color_code"
        val TAG: String = TextEditorDialog::class.java.simpleName

        fun showInstance(
            activity: AppCompatActivity,
            text: String,
            color: Int
        ): TextEditorDialog {
            val args = bundleOf(
                EXTRA_INPUT_TEXT to text,
                EXTRA_COLOR_CODE to color
            )
            val instance = TextEditorDialog()
            instance.arguments = args
            activity.supportFragmentManager.beginTransaction()
                .add(instance, TAG)
                .commit()
            return instance
        }
    }

    private val inputManager by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private var callback: TextEditorCallback? = null

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_text_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        val text = arguments?.getString(EXTRA_INPUT_TEXT)
        val color = arguments?.getInt(EXTRA_COLOR_CODE)
        addText.setText(text)
        color?.let {
            addText.setTextColor(color)
        }
    }

    private fun initView(){
        textColorSeek.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(color: Int) {
                addText.setTextColor(color)
            }
        })
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        done.setOnClickListener {
            inputManager.hideSoftInputFromWindow(requireView().windowToken, 0)
            dismiss()
            if (callback != null && addText.text.isNotBlank()) {
                callback!!.onDone(
                    newText = addText.text.toString(),
                    colorCode = textColorSeek.getColor()
                )
            }
        }
    }

    fun setCallback(textEditorCallback: TextEditorCallback) {
        callback = textEditorCallback
    }
}

interface TextEditorCallback {
    fun onDone(newText: String, colorCode: Int)
}