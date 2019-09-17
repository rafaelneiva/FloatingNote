package com.rafaelneiva.floatingnote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rafaelneiva.floatingnote.floatingview.FloatingNoteService

class MainActivity : AppCompatActivity() {

    internal lateinit var mService: FloatingNoteService
    internal var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as FloatingNoteService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                // Bind to LocalService
                val intent = Intent(this@MainActivity, FloatingNoteService::class.java)
//                bindService(intent, mConnection, Context.BIND_AUTO_CREATE) // bind service attaches the service to the activity, making the service exists only when activity is running and showing
                startService(intent)
            } else {
                Toast.makeText(
                    this,
                    "Please, permit Floating Note appears over other apps.",
                    Toast.LENGTH_LONG
                ).show()

                try {
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Your Android does not support Floating Note. It will be disabled.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        } else {
            try {
                val intent = Intent(this@MainActivity, FloatingNoteService::class.java)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onStop() {
        super.onStop()
        if (mBound)
            unbindService(mConnection)
        mBound = false
    }
}
