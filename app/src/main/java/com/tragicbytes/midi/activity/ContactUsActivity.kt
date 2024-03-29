package com.tragicbytes.midi.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class ContactUsActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        title = getString(R.string.title_contactus)
        setToolbar(toolbar)

        llCallRequest.onClick {
            dialNumber(getString(R.string.contact_phone))
        }
        llEmail.onClick {
            launchActivity<EmailActivity>()
        }

    }




}
