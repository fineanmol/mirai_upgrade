package com.tragicbytes.midi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.makeTransparentStatusBar
import kotlinx.android.synthetic.main.toolbar.*

class MyBannerScreenDetailsActivity : AppBaseActivity() {

    private var screenDataModel=ScreenDataModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_banner_screen_details)

        makeTransparentStatusBar()
        setToolbar(toolbar)
        title = "Screen Details"

        if(intent?.extras?.getSerializable(Constants.KeyIntent.DATA) != null){
            screenDataModel=intent.getSerializableExtra(Constants.KeyIntent.DATA) as ScreenDataModel
        }
    }
}