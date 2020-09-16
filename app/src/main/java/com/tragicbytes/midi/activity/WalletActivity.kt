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
import io.karn.notify.Notify
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import java.util.*


class WalletActivity : AppBaseActivity(), PaymentResultWithDataListener {
    private lateinit var dbReference: DatabaseReference
    private var requestcode= 23


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setToolbar(toolbar)
        title = getString(R.string.action_wallet)
        dbReference = FirebaseDatabase.getInstance().reference

        if (intent?.extras?.get("pending_amount") != null) {
            addAmount.setText(intent?.extras?.get("pending_amount").toString())
        }
        walletAmount.text = getStoredUserDetails().userWalletDetails.totalAmount.currencyFormat("INR")
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
                snackBar("Error Occurred")
                showProgress(false)
            })
        }
    }


    //region Payment Methods

    private fun startPayment(amountToAdd: String) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        try {
            val activity: Activity = this
            val co = Checkout()
            val PaymentAmount = amountToAdd + "00.00" //Rs 1
            //    paymentValue.text = PaymentAmount

            val options = JSONObject()
            options.put("name", "Mirai Vizion")
            options.put("description", "Refill Amount")
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
            newTransactionsDetails.transactionMessage="Transaction For Wallet ${newTransactionsDetails.transactionId}"
            Notify
                .with(this)
                .content { // this: Payload.Content.Default
                    title = "Wallet Refilled Failed"
                    text =
                        """Transaction of ${addAmount.textToString().currencyFormat("INR")} has been failed !"""
                }
                .show()
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
        snackBarError("Error $errorCode : $errorDescription")
        showProgress(false)
    }

    override fun onPaymentSuccess(rzpPaymentId: String?, paymentData: PaymentData?) {
        snackBar("Payment Successful: $rzpPaymentId ")
        showProgress(false)

        if(paymentData != null) {

            val newTransactionsDetails = TransactionDetails()
            newTransactionsDetails.transactionStatus = 1.toString()
            newTransactionsDetails.email = paymentData.userEmail
            newTransactionsDetails.transactionId = paymentData.paymentId
            newTransactionsDetails.transactionAmount = addAmount.textToString()
            newTransactionsDetails.phone = paymentData.userContact
            newTransactionsDetails.transactionMessage="Transaction For Wallet ${newTransactionsDetails.transactionId}"




            updateTransactionDetails(newTransactionsDetails, dbReference, onSuccess = {
                updateWalletAmount(dbReference, onSuccess = {
                    walletAmount.text =
                      it.currencyFormat("INR")
                    showProgress(false)

                    if (intent?.extras?.get("pending_amount") != null) {
                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        intent.extras?.remove("pending_amount")
                        finish()

                    }
                }, onFailed = {
                    snackBarError("Error Occurred")
                    showProgress(false)
                })
            }, onFailed = {
                snackBarError("Unable to process Transaction")
            })

        }
        addAmount.isEnabled=true
        addAmount.isClickable=true
        addAmount.text.clear()

        Notify
            .with(this)
            .content { // this: Payload.Content.Default
                title = "Wallet Amount Refilled"
                text =
                    """${addAmount.textToString().currencyFormat("INR")} has been successfully added to your Wallet!"""
            }
            .show()
        showProgress(false)
    }

//    override fun onStart() {
//        super.onStart()
//        showProgress(true)
//        updateWalletAmount(dbReference, onSuccess = {
//            walletAmount.text =
//                it.currencyFormat("INR")
//            showProgress(false)
//        }, onFailed = {
//            snackBar("Error Occurred")
//            showProgress(false)
//        })
//    }

    override fun onResume() {
        super.onResume()
        showProgress(true)
        updateWalletAmount(dbReference, onSuccess = {
            walletAmount.text =
                it.currencyFormat("INR")
            showProgress(false)
        }, onFailed = {
            if(it!=""){
                snackBar("Error Occurred")
            }
            showProgress(false)
        })
    }
}