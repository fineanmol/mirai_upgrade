package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.WalletTransactionDetails
import com.tragicbytes.midi.utils.extensions.rvItemAnimation
import com.tragicbytes.midi.utils.extensions.setScreenItem
import com.tragicbytes.midi.utils.extensions.setVerticalLayout
import com.tragicbytes.midi.utils.extensions.setWalletItem
import kotlinx.android.synthetic.main.activity_wallet_transactions.*
import kotlinx.android.synthetic.main.toolbar.*

class WalletTransactionsActivity : AppBaseActivity() {

    private var mTransactionDetailsAdapter: RecyclerViewAdapter<WalletTransactionDetails>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_transactions)
        setToolbar(toolbar)
        title = getString(R.string.title_transaction)

        tTransactionList.setVerticalLayout()
        setupTransactionListAdapter()

        var myBannerList=ArrayList<WalletTransactionDetails>()
        myBannerList.add(WalletTransactionDetails("","111"))
        myBannerList.add(WalletTransactionDetails("","111"))
        myBannerList.add(WalletTransactionDetails("","111"))
        myBannerList.add(WalletTransactionDetails("","111"))
        myBannerList.add(WalletTransactionDetails("","111"))
        mTransactionDetailsAdapter?.addItems(myBannerList)

    }

    private fun setupTransactionListAdapter() {
        mTransactionDetailsAdapter = RecyclerViewAdapter(R.layout.layout_transaction_card, onBind = { view, item, position -> setWalletItem(view, item) })

        tTransactionList.apply {
            adapter = mTransactionDetailsAdapter
            rvItemAnimation()
        }
        tTransactionList.adapter = mTransactionDetailsAdapter

        /* mLocationScreensAdapter?.onItemClick = { pos, view, item ->
             this.showProductDetail(item)
         }*/
    }

}