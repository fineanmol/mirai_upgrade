package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.getShortDate
import com.tragicbytes.midi.utils.extensions.getShortTime
import com.tragicbytes.midi.utils.extensions.snackBar
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
    }

    private fun setDetails(mTransactionDetails: TransactionDetails) {
        transactionId.text=mTransactionDetails.transactionId
        transactionAmount.text="$"+mTransactionDetails.transactionAmount
        transactionDate.text= getShortDate(mTransactionDetails.transactionDate)
        transactionTime.text=getShortTime(mTransactionDetails.transactionDate)

    }
}