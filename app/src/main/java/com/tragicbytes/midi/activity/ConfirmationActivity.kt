package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.activity_confirmation.rcvScreens
import kotlinx.android.synthetic.main.toolbar.*

class ConfirmationActivity : AppBaseActivity() {

    private var selectedScreens:ArrayList<ScreenDataModel> = ArrayList()

    private var mScreensAdapter:RecyclerViewAdapter<ScreenDataModel>? = null
    private var totalAmount=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        setToolbar(toolbar)
        title =getString(R.string.title_advertisement_confirmation)

        selectedScreens=intent?.getSerializableExtra("selectedScreens") as ArrayList<ScreenDataModel>

        screenCount.text="Selected Screens (${selectedScreens.size})"
        selectedScreens.forEach { screenDataModel: ScreenDataModel ->

           totalAmount +=(screenDataModel.screenPrice).toInt()
        }
        finalAmount.text=getString(R.string.RS) +" "+ totalAmount.toString()

        rcvScreens.setVerticalLayout()

        setupScreensAdapter()

        mScreensAdapter?.addItems(selectedScreens)

    }

    private fun setupScreensAdapter() {
        mScreensAdapter = RecyclerViewAdapter(
            R.layout.item_confirm_screen_card,
            onBind = { view, item, position -> setSelectedScreenItem(view, item,this) })

        rcvScreens.apply {
            adapter = mScreensAdapter
            rvItemAnimation()
        }
        rcvScreens.adapter = mScreensAdapter

        /*mScreensAdapter?.onItemClick = { pos, view, item ->
            this.selectScreen(view, item, onSelect = {
                selectedScreens.add(screensList[pos])
            },
                onUnSelect = {
                    totalAmount -= it
                    selectedScreens.remove(screensList[pos])
                })
        }*/
    }
}