package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import java.util.*


class WalletActivity : AppBaseActivity(), PaymentResultWithDataListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setToolbar(toolbar)
        title = getString(R.string.action_wallet)

        walletAmount.text =
            "$"+getStoredUserDetails().userWalletDetails.totalAmount

        loadActivity()

    }

    private fun loadActivity() {

        viewAllTransactions.onClick {
         //   launchActivity<TransactionDetailsActivity> { }
            launchActivity<WalletTransactionsActivity> { }
        }

        addMoney.onClick {
            val amountToAdd = addAmount.textToString()
            snackBar(amountToAdd)
            if (amountToAdd.isNullOrEmpty() or amountToAdd.isBlank()) {
                snackBarError("Enter Amount")
            } else {
                startPayment(amountToAdd)
            }


        }

        refreshWalletAmount.onClick {
            showProgress(true)
            snackBar("Wallet Refresh Successfully")
            showProgress(false)
        }


    }


    //region Payment Methods

    private fun startPayment(amountToAdd: String) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()


        try {
            val PaymentAmount = amountToAdd + "00.00" //Rs 1
            //    paymentValue.text = PaymentAmount

            val options = JSONObject()
            options.put("name", "Nightowl Developers")
            options.put("description", "Dominal Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://nightowldevelopers.com/img/logo.webp")
            options.put("currency", "INR")
            options.put("amount", PaymentAmount)

            val userNumber = getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_PHONE)
            val prefill = JSONObject()
            prefill.put("email", getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_EMAIL))
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
        snackBar("Error $errorCode : $errorDescription")
        showProgress(false)
    }

    override fun onPaymentSuccess(rzpPaymentId: String?, paymentData: PaymentData?) {
        snackBar("Payment Successful: $rzpPaymentId \n" + " data: ${paymentData?.orderId}")
        showProgress(true)
        val razorpayPaymentId = generateString()
            var orderid = paymentData?.orderId
            var payid = paymentData?.paymentId
            var signid = paymentData?.signature
            var userid = paymentData?.userContact
            var emailid = paymentData?.userEmail

        if(paymentData != null) {
            getSharedPrefInstance().setValue(Constants.WalletTransactionDetails.orderId, paymentData.orderId)
            getSharedPrefInstance().setValue(Constants.WalletTransactionDetails.transactionId, rzpPaymentId)
            getSharedPrefInstance().setValue(Constants.WalletTransactionDetails.signature, paymentData.signature)
            getSharedPrefInstance().setValue(Constants.WalletTransactionDetails.transactionId, paymentData.userEmail)
            getSharedPrefInstance().setValue(Constants.WalletTransactionDetails.transactionId, paymentData.userContact)

            updateWalletAmount()
        }
        showProgress(false)

    }

    private fun updateWalletAmount() {
        try {
            val updatedWallet =
                getSharedPrefInstance().getStringValue(Constants.WalletTransactionDetails.WalletAmountUpdated)
                    .toInt() + 100
            getSharedPrefInstance().setValue(
                Constants.WalletTransactionDetails.WalletAmountUpdated,
                updatedWallet.toString()
            )
                .toString()
        } catch (e: Exception) {
            snackBarError("Error: " + e.message)
        }
        showProgress(false)
    }


    private fun generateString(): String? {
        val uuid: String = UUID.randomUUID().toString()
        return uuid.replace("-".toRegex(), "")
    }


}