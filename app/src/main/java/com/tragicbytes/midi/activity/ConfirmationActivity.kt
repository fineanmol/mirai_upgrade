package com.tragicbytes.midi.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import kotlinx.android.synthetic.main.toolbar.*

class ConfirmationActivity : AppBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        setToolbar(toolbar)
        title =getString(R.string.title_advertisement_confirmation)

    }
}