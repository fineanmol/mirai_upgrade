package com.tragicbytes.midi.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.ProductImageAdapter
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.activity_confirmation.ivBack

class ConfirmationActivity : AppBaseActivity() {

    private var ongoingAdv = SingleAdvertisementDetails()
   // private lateinit var mMainBinding: ActivityProductDetailBinding

    private var mScreensAdapter: RecyclerViewAdapter<ScreenDataModel>? = null
    private var totalScreenPrice = 0
    private var finalAmountPrice = 0

    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
      //  setToolbarWithoutBackButton(mMainBinding.toolbar)

        toolbar_layoutBtn.title = "Advertisement Confirmation"
        ivBack.onClick {
            onBackPressed()
        }
        /*title = getString(R.string.title_advertisement_confirmation)*/
        dbReference = FirebaseDatabase.getInstance().reference

        ongoingAdv = intent?.getSerializableExtra("ongoing_adv") as SingleAdvertisementDetails


        adv_start_date_time.text="Start Date: ${getShortDate(ongoingAdv.startFrom.toLong())}, ${getShortTime(ongoingAdv.startFrom.toLong())}"
        adv_end_date_time.text="End Date: ${getShortDate(ongoingAdv.endOn.toLong())}, ${getShortTime(ongoingAdv.endOn.toLong())}"
        adv_gender_pref.text = "Gender      :${ongoingAdv.advGenderPref}"
        adv_age_group_pref.text = "Age Group:${ongoingAdv.advAgePref[0]}"

        screenCount.text = "Selected Screens (${ongoingAdv.screens.size})"
        ongoingAdv.screens.forEach { screenDataModel: ScreenDataModel ->

            totalScreenPrice += (screenDataModel.screenPrice).toInt()
        }

        finalpayAmount.text = totalScreenPrice.toString().currencyFormat("INR")

        rcvScreens.setVerticalLayout()

        setupScreensAdapter()

        mScreensAdapter?.addItems(ongoingAdv.screens)


        if (totalScreenPrice > getStoredUserDetails().userWalletDetails.totalAmount.toInt())
            tPayBtn.text =
                """Add ${"${totalScreenPrice - getStoredUserDetails().userWalletDetails.totalAmount.toInt()}".currencyFormat()} to Place Order"""
        else
            tPayBtn.text = "Place Order"

        tPayBtn.onClick {
            if (totalScreenPrice > getStoredUserDetails().userWalletDetails.totalAmount.toInt()) {
                launchActivity<WalletActivity>((21)) {
                    putExtra(
                        "pending_amount",
                        totalScreenPrice - getStoredUserDetails().userWalletDetails.totalAmount.toInt()
                    )
                }
            } else {
                placeOrder(ongoingAdv, dbReference)
            }


            /*launchActivity<WalletActivity> {
            if(finalAmountPrice>0)
            putExtra("amountNeedForAdv",finalAmountPrice.toString()) }*/
        }
        val myImages = ArrayList<String>()
        myImages.add(ongoingAdv.advBannerUrl)
        val imageAdapter = ProductImageAdapter(myImages)
        confirmationViewPager.adapter = null
        confirmationViewPager.adapter = imageAdapter
        confirmationViewPager.adapter?.notifyDataSetChanged()
    }

    private fun placeOrder(
        onGoingAdv: SingleAdvertisementDetails,
        dbReference: DatabaseReference
    ) {
        var newTransactionsDetails = TransactionDetails()
        newTransactionsDetails.transactionStatus = 2.toString()
        newTransactionsDetails.email = getStoredUserDetails().userPersonalDetails.email
        newTransactionsDetails.transactionId = "pay_ORDER001"
        newTransactionsDetails.transactionAmount = (-totalScreenPrice).toString()
        newTransactionsDetails.orderId = onGoingAdv.advId.toString()
        newTransactionsDetails.phone = getStoredUserDetails().userPersonalDetails.phone
        newTransactionsDetails.transactionMessage="Transaction For Banner ${newTransactionsDetails.transactionId}"

        updateTransactionDetails(newTransactionsDetails, dbReference, onSuccess = {
            updateWalletAmount(dbReference, onSuccess = {
                showProgress(false)
                var localUserData = getStoredUserDetails()
                var advList = localUserData.userAdvertisementDetails.singleAdvertisementDetails
                onGoingAdv.advCost=totalScreenPrice.toString()
                advList.add(onGoingAdv)
                localUserData.userAdvertisementDetails.singleAdvertisementDetails = advList
                snackBar("Payment Processed. Processing Advertisement")
                dbReference.child("UsersData/${getStoredUserDetails().userId}")
                    .setValue(localUserData).addOnSuccessListener {
                        dbReference.child("UsersData/${getStoredUserDetails().userId}/userAdvertisementDetails/singleAdvertisementDetails/${localUserData.userAdvertisementDetails.singleAdvertisementDetails.size - 1}/advSubmittedOn")
                            .setValue(
                                ServerValue.TIMESTAMP
                            ).addOnSuccessListener {
                                snackBar("Congrats! Your Advertisement Submitted for Approval.",Snackbar.LENGTH_LONG)
                            }
                            .addOnFailureListener {
                                snackBarError("Error Occurred")
                            }
                    }
            }, onFailed = {
                snackBarError("Error Occurred")
                showProgress(false)
            })
        }, onFailed = {
            snackBarError("Unable to process Transaction")
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check that it is the SecondActivity with an OK result
        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                if (totalScreenPrice > getStoredUserDetails().userWalletDetails.totalAmount.toInt())
                    tPayBtn.text =
                        """Add ${"${totalScreenPrice - getStoredUserDetails().userWalletDetails.totalAmount.toInt()}".currencyFormat()} to Place Order"""
                else
                    tPayBtn.text = "Place Order"
            }
        }
    }

    private fun setupScreensAdapter() {
        mScreensAdapter = RecyclerViewAdapter(
            R.layout.item_confirm_screen_card,
            onBind = { view, item, position -> setSelectedScreenItem(view, item, this) })

        rcvScreens.apply {
            adapter = mScreensAdapter
            rvItemAnimation()
        }
        rcvScreens.adapter = mScreensAdapter
    }
}