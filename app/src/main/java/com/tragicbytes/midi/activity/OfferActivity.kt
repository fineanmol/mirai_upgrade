package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import kotlinx.android.synthetic.main.toolbar.*

class OfferActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_screen_order_details)
        setToolbar(toolbar)
        title = "Screen Order Details"


    }




}