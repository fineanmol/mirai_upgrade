package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_confirmation.*
import kotlinx.android.synthetic.main.activity_confirmation.rcvScreens
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject

class ConfirmationActivity : AppBaseActivity(),PaymentResultWithDataListener {

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

        tPayBtn.onClick { startPayment(totalAmount) }

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

    //region Payment Methods

    private fun startPayment(amountToPay: Int) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()


        try {
            val paymentAmount = "$amountToPay" + "00.00" //Rs 1
            //    paymentValue.text = PaymentAmount

            val options = JSONObject()
            options.put("name", "Nightowl Developers")
            options.put("description", "Dominal Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://nightowldevelopers.com/img/logo.webp")
            options.put("currency", "INR")
            options.put("amount", paymentAmount)

            val userNumber = getStoredUserDetails().userPersonalDetails.phone
            val prefill = JSONObject()
            prefill.put("email", getStoredUserDetails().userPersonalDetails.email)
            if (userNumber != null) prefill.put("contact", userNumber) else prefill.put("contact", "9876543210")


            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            snackBarError("Error in payment: " + e.message)
            e.printStackTrace()
        }
    }

    override fun onPaymentError(
        errorCode: Int,
        errorDescription: String?,
        paymentData: PaymentData?
    ) {
        /*if (paymentData != null) {
            updateTransactionDetails(paymentData,0)
        }*/
       /* addAmount.isEnabled=true
        addAmount.isClickable=true*/
        snackBar("Error $errorCode : $errorDescription")
        showProgress(false)
    }

    override fun onPaymentSuccess(rzpPaymentId: String?, paymentData: PaymentData?) {
        snackBar("Payment Successful: $rzpPaymentId \n" + " data: ${paymentData?.orderId}")
        showProgress(false)

      /*  if(paymentData != null) {
            updateTransactionDetails(paymentData,1)
            updateWalletAmount()
        }*/
       /* addAmount.isEnabled=true
        addAmount.isClickable=true*/
        showProgress(false)

    }
}