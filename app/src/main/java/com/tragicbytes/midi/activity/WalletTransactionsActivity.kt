package com.tragicbytes.midi.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.database.*
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.TransactionDetails
import com.tragicbytes.midi.models.UserWalletDetails
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_wallet_transactions.*
import kotlinx.android.synthetic.main.toolbar.*

class WalletTransactionsActivity : AppBaseActivity() {

    private var mTransactionDetailsAdapter: RecyclerViewAdapter<TransactionDetails>? = null
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_transactions)
        setToolbar(toolbar)
        title = getString(R.string.title_transaction)
        dbReference = FirebaseDatabase.getInstance().reference

        supportActionBar?.setIcon(R.drawable.ic_baseline_bubble_chart_24)

        tTransactionList.setVerticalLayout()
        setupTransactionListAdapter()

        getTransactionListList()

    }

    private fun getTransactionListList() {
        dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var userWalletDetails=
                            dataSnapshot.getValue(UserWalletDetails::class.java)!!
                            if(userWalletDetails.transactionsDetails.isNotEmpty()) noTransactions.visibility=View.GONE
                            var storedUserDetails=getStoredUserDetails()
                            storedUserDetails.userWalletDetails=userWalletDetails
                            updateStoredUserDetails(storedUserDetails)
                            mTransactionDetailsAdapter?.addItems(ArrayList(userWalletDetails.transactionsDetails))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("Unable to fetch locations")
                    }
                }

            )
    }

    private fun setupTransactionListAdapter() {
        mTransactionDetailsAdapter = RecyclerViewAdapter(R.layout.layout_transaction_card, onBind = { view, item, position -> setWalletItem(view, item) })

        tTransactionList.apply {
            adapter = mTransactionDetailsAdapter
            rvItemAnimation()
        }
        tTransactionList.adapter = mTransactionDetailsAdapter

        mTransactionDetailsAdapter?.onItemClick = { pos, view, item ->
             this.showTransactionDetail(item)
         }
    }

}