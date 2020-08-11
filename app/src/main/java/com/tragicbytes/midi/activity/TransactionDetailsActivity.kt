package com.tragicbytes.midi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import kotlinx.android.synthetic.main.toolbar.*

class TransactionDetailsActivity : AppBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        setToolbar(toolbar)
        title = getString(R.string.title_transaction)
    }
}