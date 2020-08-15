package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.extensions.launchActivity
import com.tragicbytes.midi.utils.extensions.onClick
import com.tragicbytes.midi.utils.extensions.snackBar
import com.tragicbytes.midi.utils.extensions.textToString
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar.*


class WalletActivity : AppBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setToolbar(toolbar)
        title=getString(R.string.action_wallet)
       /* addFragment(mWalletFragment, R.id.fragmentContainer)*/
        viewAllTransactions.onClick {
            launchActivity<TransactionDetailsActivity> {  }
        }

        var amountToAdd= addAmount.textToString()
        snackBar(amountToAdd)

    }

}