package com.tragicbytes.midi.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject


class WalletActivity : AppBaseActivity(), PaymentResultWithDataListener {
    private lateinit var dbReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setToolbar(toolbar)
        title = getString(R.string.action_wallet)
        dbReference = FirebaseDatabase.getInstance().reference

        if (intent?.extras?.get("pending_amount") != null) {
            addAmount.setText(intent?.extras?.get("pending_amount").toString())
        }
        walletAmount.text =
            getStoredUserDetails().userWalletDetails.totalAmount.currencyFormat("INR")
        loadActivity()
    }

    private fun loadActivity() {

        viewAllTransactions.onClick {
         //   launchActivity<TransactionDetailsActivity> { }
            launchActivity<WalletTransactionsActivity> { }
        }

        addMoney.onClick {
            val amountToAdd = addAmount.textToString()
            if (amountToAdd.isNullOrEmpty() or amountToAdd.isBlank()) {
                snackBarError("Enter Amount")
            } else {
                addAmount.isEnabled=false
                addAmount.isClickable=false
                startPayment(amountToAdd)
            }


        }

        refreshWalletAmount.onClick {
            showProgress(true)
            updateWalletAmount(dbReference, onSuccess = {
                walletAmount.text =
                    it.currencyFormat("INR")
                showProgress(false)
                snackBar("Wallet Refresh Successfully")
            }, onFailed = {
                snackBar("Error Occured")
                showProgress(false)
            })
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

            val userNumber = getStoredUserDetails().userPersonalDetails.phone
            val prefill = JSONObject()
            prefill.put("email", getStoredUserDetails().userPersonalDetails.email)
            if (userNumber != null) prefill.put("contact", userNumber) else prefill.put(
                "contact",
                "9876543210"
            )


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
        if (paymentData != null) {
            val newTransactionsDetails = TransactionDetails()
            newTransactionsDetails.transactionStatus = 0.toString()
            newTransactionsDetails.email = paymentData.userEmail
            newTransactionsDetails.transactionId = paymentData.paymentId
            newTransactionsDetails.transactionAmount = addAmount.textToString()
            newTransactionsDetails.phone = paymentData.userContact
            updateTransactionDetails(newTransactionsDetails, dbReference, onSuccess = {
                updateWalletAmount(dbReference, onSuccess = {
                    walletAmount.text =
                         it.currencyFormat("INR")
                    showProgress(false)
                    snackBar("Wallet Refresh Successfully")
                }, onFailed = {
                    snackBar("Error Occured")
                    showProgress(false)
                })
            }, onFailed = {
                snackBar("Unable to process Transaction")
            })
        }
        addAmount.isEnabled=true
        addAmount.isClickable=true
        snackBar("Error $errorCode : $errorDescription")
        showProgress(false)
    }

    override fun onPaymentSuccess(rzpPaymentId: String?, paymentData: PaymentData?) {
        snackBar("Payment Successful: $rzpPaymentId \n" + " data: ${paymentData?.orderId}")
        showProgress(false)

        if(paymentData != null) {

            val newTransactionsDetails = TransactionDetails()
            newTransactionsDetails.transactionStatus = 1.toString()
            newTransactionsDetails.email = paymentData.userEmail
            newTransactionsDetails.transactionId = paymentData.paymentId
            newTransactionsDetails.transactionAmount = addAmount.textToString()
            newTransactionsDetails.phone = paymentData.userContact

            updateTransactionDetails(newTransactionsDetails, dbReference, onSuccess = {
                updateWalletAmount(dbReference, onSuccess = {
                    walletAmount.text =
                      it.currencyFormat("INR")
                    showProgress(false)
                    snackBar("Wallet Refresh Successfully")
                    if (intent?.extras?.get("pending_amount") != null) {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }, onFailed = {
                    snackBar("Error Occured")
                    showProgress(false)
                })
            }, onFailed = {
                snackBar("Unable to process Transaction")
            })

        }
        addAmount.isEnabled=true
        addAmount.isClickable=true
        showProgress(false)
    }
}