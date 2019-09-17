package com.rafaelneiva.floatingnote.floatingview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.rafaelneiva.floatingnote.R
import com.rafaelneiva.floatingnote.databinding.ViewFloatingAnchorBinding

/**
 * Created by rafaelneiva on 06/03/18.
 */

class FloatingAnchor : FrameLayout {

    private lateinit var bind: ViewFloatingAnchorBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        bind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_floating_anchor,
            this@FloatingAnchor,
            true
        );
    }

    fun setClickListener(clickListener: OnClickListener?) {
        bind.btNewNote.setOnClickListener(clickListener);
    }

    fun setTouchListener(touchListener: OnTouchListener?) {
        bind.btNewNote.setOnTouchListener(touchListener);
    }

    override fun performClick(): Boolean {
        bind.btNewNote.performClick()
        return super.performClick()
    }

}
