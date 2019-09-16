package com.rafaelneiva.floatingnote.floatingview

import android.animation.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

/**
 * Created by rafaelneiva on 08/03/18.
 */

class FloatingWindow(private val mContext: Context) : View.OnTouchListener, View.OnClickListener {

    // touch
    private var lastAction: Int = 0
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0.toFloat()
    private var initialTouchY: Float = 0.toFloat()

    private val mWindowManager: WindowManager?
    private val mParams: WindowManager.LayoutParams
    private val mAnchor: FeedbackAnchor

    private val mDisplaySize = Point()

    private var mAnimating: Boolean = false
    // True if the window is clinging to the left side of the screen; false
    // if the window is clinging to the right side of the screen.
    private var mIsOnLeft: Boolean = false
    private var mScreenRoot: View? = null
    internal var arrastadinha = false // verify is user moves the anchor

    init {
        mWindowManager = mContext.getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.defaultDisplay?.getSize(mDisplaySize)

        mParams = createWindowParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        mAnchor = FeedbackAnchor(mContext)

        enableTouch()

        Handler().postDelayed({ this.makeDiscreet() }, 400)

        // the window starts on left
        mIsOnLeft = true
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (mAnimating) {
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                //remember the initial position.
                initialX = mParams.x
                initialY = mParams.y

                //get the touch location
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                lastAction = event.action

                makeNormal()

                return true
            }
            MotionEvent.ACTION_UP -> {
                //As we implemented on touch listener with ACTION_MOVE,
                //we have to check if the previous action was ACTION_DOWN
                //to identify if the user clicked the view or not.
                //                if (lastAction == MotionEvent.ACTION_DOWN) {
                //                    mAnchor.bind.btFeedback.performClick();
                //                }
                if (!arrastadinha || lastAction == MotionEvent.ACTION_DOWN) {
                    //                    mAnchor.bind.btFeedback.performClick();
                }
                lastAction = event.action

                val screenWidth = mDisplaySize.x
                val windowWidth = mAnchor.width
                val oldX = mParams.x

                if (oldX + windowWidth / 2 < screenWidth / 2) {
                    snap(oldX, 0)
                    mIsOnLeft = true
                } else {
                    snap(oldX, screenWidth - windowWidth)
                    mIsOnLeft = false
                }

                makeDiscreet()

                return true
            }
            MotionEvent.ACTION_MOVE -> {

                //Calculate the X and Y coordinates of the view.
                val newX = initialX + (event.rawX - initialTouchX).toInt()
                val newY = initialY + (event.rawY - initialTouchY).toInt()

                arrastadinha =
                    Math.abs(event.rawX - initialTouchX) > mAnchor.width / 4 || Math.abs(event.rawY - initialTouchY) > mAnchor.height / 4

                updateWindowPosition(newX, newY)

                lastAction = event.action
                return true
            }
        }
        return false
    }

    override fun onClick(v: View) {
        // todo
    }

    fun show() {
        mWindowManager?.addView(mAnchor, mParams)
    }

    fun hide() {
        mWindowManager?.removeView(mAnchor)
    }

    fun onConfigurationChanged(newConfiguration: Configuration) {
        mWindowManager!!.defaultDisplay.getSize(mDisplaySize)
    }

    private fun updateWindowPosition(x: Int, y: Int) {
        mParams.x = x
        mParams.y = y
        mWindowManager!!.updateViewLayout(mAnchor, mParams)
    }

    private fun snap(fromX: Int, toX: Int) {
        val snapAnimator = ValueAnimator.ofFloat(0f, 1f)
        snapAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mAnchor.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                mAnimating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                mAnchor.setLayerType(View.LAYER_TYPE_NONE, null)
                mAnimating = false
            }
        })
        snapAnimator.addUpdateListener { animation ->
            val currX = fromX + (animation.animatedFraction * (toX - fromX)).toInt()
            updateWindowPosition(currX, mParams.y)
        }
        snapAnimator.interpolator = DecelerateInterpolator()
        snapAnimator.duration = 250
        snapAnimator.start()
    }

    private fun makeDiscreet() {
        val set = AnimatorSet()
        set.duration = 600
        set.interpolator = AccelerateInterpolator()
        set.startDelay = 400

        val alpha = ObjectAnimator.ofFloat<View>(mAnchor, View.ALPHA, 1f, 0.25f)
        val scaleX = ObjectAnimator.ofFloat<View>(mAnchor, View.SCALE_X, 1f, 0.75f)
        val scaleY = ObjectAnimator.ofFloat<View>(mAnchor, View.SCALE_Y, 1f, 0.75f)

        mAnchor.pivotX = (if (mIsOnLeft) 0 else mAnchor.width).toFloat()

        set.playTogether(alpha, scaleX, scaleY)
        set.start()
    }

    private fun makeNormal() {
        val set = AnimatorSet()
        set.duration = 400

        val alpha = ObjectAnimator.ofFloat<View>(mAnchor, View.ALPHA, 0.25f, 1f)
        val scaleX = ObjectAnimator.ofFloat<View>(mAnchor, View.SCALE_X, 0.75f, 1f)
        val scaleY = ObjectAnimator.ofFloat<View>(mAnchor, View.SCALE_Y, 0.75f, 1f)

        set.playTogether(alpha, scaleX, scaleY)
        set.start()
    }

    fun sendScreenshot(viewRoot: View) {
        mScreenRoot = viewRoot
    }

    fun disableTouch() {
        mAnchor.setClickListener(null)
        mAnchor.setTouchListener(null)
    }

    fun enableTouch() {
        mAnchor.setClickListener(this)
        mAnchor.setTouchListener(this)
    }

    internal inner class MessageReceiver : ResultReceiver(null) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultCode != RESULT_OK) {
                return
            }
            val message = resultData.getString(KEY_MESSAGE)
            Log.i("Test", message!!)

            enableTouch()
        }

    }// Pass in a handler or null if you don't care about the thread on which your code is executed.

    companion object {

        private fun createWindowParams(width: Int, height: Int): WindowManager.LayoutParams {
            val params = WindowManager.LayoutParams()
            params.width = width
            params.height = height

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }

            params.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
            params.format = PixelFormat.TRANSLUCENT
            params.gravity = Gravity.START or Gravity.TOP
            return params
        }

        val KEY_RECEIVER = "KEY_RECEIVER"
        val KEY_MESSAGE = "KEY_MESSAGE"
    }
}
