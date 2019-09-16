package com.rafaelneiva.floatingnote.floatingview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.rafaelneiva.floatingnote.R
import com.rafaelneiva.floatingnote.databinding.ViewFeedbackAnchorBinding

/**
 * Created by rafaelneiva on 06/03/18.
 */

class FeedbackAnchor : FrameLayout {

    private lateinit var bind: ViewFeedbackAnchorBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        bind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_feedback_anchor,
            this@FeedbackAnchor,
            true
        );
    }

    fun setClickListener(clickListener: OnClickListener?) {
        bind.btFeedback.setOnClickListener(clickListener);
    }

    fun setTouchListener(touchListener: OnTouchListener?) {
        bind.btFeedback.setOnTouchListener(touchListener);
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}
