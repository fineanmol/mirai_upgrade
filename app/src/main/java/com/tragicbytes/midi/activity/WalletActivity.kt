package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.TransactionsDetails
import com.tragicbytes.midi.models.UserWalletDetails
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.activity_wallet_transactions.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import java.util.*


class WalletActivity : AppBaseActivity(), PaymentResultWithDataListener {
    private lateinit var dbReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setToolbar(toolbar)
        title = getString(R.string.action_wallet)
        dbReference = FirebaseDatabase.getInstance().reference


        walletAmount.text = getString(R.string.rs) + getStoredUserDetails().userWalletDetails.totalAmount

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
            updateWalletAmount()
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
        if (paymentData != null) {
            updateTransactionDetails(paymentData,0)
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
            updateTransactionDetails(paymentData,1)
            updateWalletAmount()
        }
        addAmount.isEnabled=true
        addAmount.isClickable=true
        showProgress(false)

    }

    private fun updateTransactionDetails(paymentData: PaymentData,status:Int) {
        var newTransactionsDetails=TransactionsDetails()
        newTransactionsDetails.transactionStatus="1"
        newTransactionsDetails.email=paymentData.userEmail
        newTransactionsDetails.transactionId=paymentData.paymentId
        newTransactionsDetails.transactionAmount=addAmount.textToString()
        newTransactionsDetails.phone=paymentData.userContact

        var localStoredUserDetails=getStoredUserDetails()
        var transactionsList=localStoredUserDetails.userWalletDetails.transactionsDetails
        transactionsList.add(newTransactionsDetails)
        localStoredUserDetails.userWalletDetails.transactionsDetails=transactionsList
        updateStoredUserDetails(localStoredUserDetails)
        dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails").setValue(transactionsList)
        dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails/${transactionsList.size-1}/transactionDate").setValue(ServerValue.TIMESTAMP)
    }

    private fun updateWalletAmount() {
        try {
            dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails")
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                var sum=0
                                dataSnapshot.children.forEach {
                                    var transactionsDetails=it.getValue(TransactionsDetails::class.java)!!
                                    if(transactionsDetails.transactionStatus=="1"){
                                        sum += transactionsDetails.transactionAmount.toInt()
                                    }
                                }
                                dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/totalAmount")
                                    .setValue(sum.toString()).addOnCompleteListener{
                                        var localUserDetails= getStoredUserDetails()
                                        localUserDetails.userWalletDetails.totalAmount=sum.toString()
                                        updateStoredUserDetails(localUserDetails)
                                        walletAmount.text="$"+sum.toString()
                                        snackBar("Wallet Refresh Successfully")
                                    }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            snackBar("Error Occured")
                        }
                    }

                )
        } catch (e: Exception) {
            snackBarError("Error: " + e.message)
        }
        showProgress(false)
    }
}