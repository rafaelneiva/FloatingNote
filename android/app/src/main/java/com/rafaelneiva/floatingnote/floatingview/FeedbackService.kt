package com.rafaelneiva.floatingnote.floatingview

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.view.View

/**
 * Created by rafaelneiva on 06/03/18.
 */

class FeedbackService : Service() {

    private val mBinder = LocalBinder()

    private var mWindow: FloatingWindow? = null

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: FeedbackService
            get() = this@FeedbackService
    }

    override fun onCreate() {
        super.onCreate()

        mWindow = FloatingWindow(this)
        mWindow!!.show()
    }

    fun sendScreenshot(viewRoot: View) {
        mWindow!!.sendScreenshot(viewRoot)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindow!!.hide()
        mWindow = null
    }

    override fun onConfigurationChanged(newConfiguration: Configuration) {
        mWindow!!.onConfigurationChanged(newConfiguration)
    }

}
