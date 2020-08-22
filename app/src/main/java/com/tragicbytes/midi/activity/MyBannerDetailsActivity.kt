package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_my_banner_details.*
import kotlinx.android.synthetic.main.toolbar.*

class MyBannerDetailsActivity : AppBaseActivity() {

    private var singleAdvertisementDetails=SingleAdvertisementDetails()

    private var mLocationScreensAdapter: RecyclerViewAdapter<ScreenDataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_banner_details)

        makeTransparentStatusBar()
        setToolbar(toolbar)
        title = "Banner Details"


        if(intent?.extras?.getSerializable(Constants.KeyIntent.DATA) != null){
            singleAdvertisementDetails=intent!!.getSerializableExtra(Constants.KeyIntent.DATA)  as SingleAdvertisementDetails
            bindData(singleAdvertisementDetails)
            bannerScreensList.setVerticalLayout()

            setupLocationScreensAdapter()

            mLocationScreensAdapter?.addItems(singleAdvertisementDetails.screens)

            title = singleAdvertisementDetails.advId.toString()

        }
    }

    private fun bindData(singleAdvertisementDetails: SingleAdvertisementDetails) {
        orderId.text=singleAdvertisementDetails.advId
        bAgeGroup.text=singleAdvertisementDetails.advAgePref.toString()
        bStartDate.text= getShortDate(singleAdvertisementDetails.startFrom.toLong())
        bEndDate.text=getShortDate(singleAdvertisementDetails.endOn.toLong())
    }

    private fun setupLocationScreensAdapter() {
        mLocationScreensAdapter = RecyclerViewAdapter(
            R.layout.item_my_screen_card,
            onBind = { view, item, position -> setOrderedScreenData(view, item,this) })

        bannerScreensList.apply {
            adapter = mLocationScreensAdapter
            rvItemAnimation()
        }
        bannerScreensList.adapter = mLocationScreensAdapter

        mLocationScreensAdapter?.onItemClick = { pos, view, item ->
            showMyBannerScreenDetails(item)
        }
    }
}