package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.activity_confirmation.rcvScreens
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject

class ConfirmationActivity : AppBaseActivity() {

    private var ongoingAdv = SingleAdvertisementDetails()

    private var mScreensAdapter:RecyclerViewAdapter<ScreenDataModel>? = null
    private var totalScreenPrice=0
    private var finalAmountPrice=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        setToolbar(toolbar)
        title =getString(R.string.title_advertisement_confirmation)

        ongoingAdv=intent?.getSerializableExtra("ongoing_adv") as SingleAdvertisementDetails


        adv_start_date_time.text="Start Date: ${getShortDate(ongoingAdv.startFrom.toLong())}, ${getShortTime(ongoingAdv.startFrom.toLong())}"
        adv_end_date_time.text="End Date: ${getShortDate(ongoingAdv.endOn.toLong())}, ${getShortTime(ongoingAdv.endOn.toLong())}"
        adv_gender_pref.text="Gender      :${ongoingAdv.advGenderPref}"
        adv_age_group_pref.text="Age Group:${ongoingAdv.advAgePref[0]}"


        screenCount.text="Selected Screens (${ongoingAdv.screens.size})"
        ongoingAdv.screens.forEach { screenDataModel: ScreenDataModel ->

           totalScreenPrice +=(screenDataModel.screenPrice).toInt()
        }

        val userWalletAmount = getStoredUserDetails().userWalletDetails.totalAmount

        finalScreenAmount.text= totalScreenPrice.toString().currencyFormat("INR")
        walletBalance.text = userWalletAmount.currencyFormatNegative("INR")

        finalAmountPrice = if(totalScreenPrice > userWalletAmount.toInt())
            totalScreenPrice - userWalletAmount.toInt()
        else 0
        finalAmount.text = finalAmountPrice.toString().currencyFormat("INR")

        rcvScreens.setVerticalLayout()

        setupScreensAdapter()

        mScreensAdapter?.addItems(ongoingAdv.screens)

        if(finalAmountPrice.toInt()>0)
            tPayBtn.text = """Add ${"$finalAmountPrice".currencyFormat()} to Place Order"""
        else
            tPayBtn.text ="Quick Pay"

        tPayBtn.onClick {

            launchActivity<WalletActivity> {
            if(finalAmountPrice>0)
            putExtra("amountNeedForAdv",finalAmountPrice.toString()) }
        }

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


    }



}