package com.rafaelneiva.floatingnote.floatingview

import android.app.Activity
import android.os.Bundle
import android.os.ResultReceiver
import androidx.appcompat.app.AppCompatActivity
import com.rafaelneiva.floatingnote.R

class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun finish() {
        val receiver = intent.getParcelableExtra<ResultReceiver>(FloatingWindow.KEY_RECEIVER)

        val resultData = Bundle()
        resultData.putString(FloatingWindow.KEY_MESSAGE, "Success")
        receiver!!.send(Activity.RESULT_OK, resultData)

        super.finish()
    }
}
