package com.tragicbytes.midi.activity

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_offer.*
import kotlinx.android.synthetic.main.item_offer.view.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class OfferActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_order_details)
        setToolbar(toolbar)
        title = "Screen Order Details"


    }




}