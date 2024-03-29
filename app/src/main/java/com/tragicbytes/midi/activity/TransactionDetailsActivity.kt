package com.tragicbytes.midi.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.activity_transaction_details.*
import kotlinx.android.synthetic.main.toolbar.*

class TransactionDetailsActivity : AppBaseActivity() {

    private var mTransactionDetails: TransactionDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        setToolbar(toolbar)
        title =getString(R.string.title_transaction)

        when {
            intent?.extras?.getSerializable(Constants.KeyIntent.DATA) != null -> {
                mTransactionDetails = intent.getSerializableExtra(Constants.KeyIntent.DATA) as TransactionDetails
                setDetails(mTransactionDetails!!)
            }
            else->{
                snackBar("Error!Transaction Details Not Found.")

            }
        }

        tansactionSupportBtn.onClick {
             try {
                 val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.text_iqonicdesign_gmail_com), null))
                 emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Need help with Transaction "+ (mTransactionDetails?.transactionId))
                 emailIntent.putExtra(Intent.EXTRA_TEXT, "I am having an issue regarding the transaction ")
                 startActivity(Intent.createChooser(emailIntent, "Send email..."))
             } catch (e: Exception) {
                 snackBarError("Please Connect us via Help Section")
             }
        }
    }

    private fun setDetails(mTransactionDetails: TransactionDetails) {
        transactionId.text=mTransactionDetails.transactionId
        transactionDate.text= getShortDate(mTransactionDetails.transactionDate)
        transactionTime.text=getShortTime(mTransactionDetails.transactionDate)
        transactionEmail.text = mTransactionDetails.email

        when (mTransactionDetails.transactionStatus) {
            "1" -> {
                transactionRemark.text="Money Added to Wallet"
                transactionAmount.text=mTransactionDetails.transactionAmount.currencyFormat("INR")
                transactionIcon.isVisible()

            }
            "0" -> {
                transactionAmount.text=mTransactionDetails.transactionAmount.currencyFormat("INR")
                transactionRemark.text="Add Money Failed"
                transactionAmount.setTextColor(resources.getColor(R.color.red))
                transactionRemark.setTextColor(resources.getColor(R.color.red))
                topRelativeLayout.setBackgroundColor(resources.getColor(R.color.red))
                transactionIcon.isGone()
            }
            "2" -> {
                transactionAmount.text="- ₹"+ mTransactionDetails.transactionAmount.split("-").last().toString()
                transactionRemark.text="Money Paid to Advertisement Id : ${mTransactionDetails.orderId}"
                transactionAmount.setTextColor(resources.getColor(R.color.green_dark))
                transactionRemark.setTextColor(resources.getColor(R.color.green_dark))
                topRelativeLayout.setBackgroundColor(resources.getColor(R.color.green_dark))
                transactionIcon.isGone()
            }
            "3" -> {
                transactionAmount.text="+ ₹"+ mTransactionDetails.transactionAmount.split("-").last().toString()
                transactionRemark.text="Money Reverted to your wallet for Id : ${mTransactionDetails.orderId}"
                transactionAmount.setTextColor(resources.getColor(R.color.green_dark))
                transactionRemark.setTextColor(resources.getColor(R.color.green_dark))
                topRelativeLayout.setBackgroundColor(resources.getColor(R.color.green_dark))
                transactionIcon.isGone()
            }
            else -> transactionRemark.text=""
        }

    }



}