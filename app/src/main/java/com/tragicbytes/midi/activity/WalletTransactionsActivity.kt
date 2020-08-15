package com.tragicbytes.midi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.models.WalletTransactionDetails
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_wallet_transactions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar.*

class WalletTransactionsActivity : AppBaseActivity() {

    private var mTransactionDetailsAdapter: RecyclerViewAdapter<WalletTransactionDetails>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_transactions)
        setToolbar(toolbar)
        title = getString(R.string.title_transaction)

        setupTransactionListAdapter()

    }

    private fun setupTransactionListAdapter() {

        mTransactionDetailsAdapter = RecyclerViewAdapter(R.layout.layout_transaction_card, onBind = { view, item, position -> setWalletItem(view, item) })
        tTransactionList.apply {
           /* layoutManager = GridLayoutManager(activity, 2, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mTransactionDetailsAdapter
            rvItemAnimation()*/
        }
    }
}