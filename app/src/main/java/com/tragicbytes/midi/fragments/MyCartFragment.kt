package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.DashBoardActivity
import com.tragicbytes.midi.activity.MyCartActivity
import com.tragicbytes.midi.activity.OrderSummaryActivity
import com.tragicbytes.midi.activity.ProductDetailActivity
import com.tragicbytes.midi.base.BaseRecyclerAdapter
import com.tragicbytes.midi.databinding.ItemCartBinding
import com.tragicbytes.midi.models.CartResponse
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.utils.Constants.KeyIntent.PRODUCT_ID
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*

class MyCartFragment : BaseFragment() {

    var onNetworkRetry: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_my_banners, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        refreshLayout.setOnRefreshListener {
            loadApis()
            refreshLayout.isRefreshing=false
        }
        refreshLayout.viewTreeObserver.addOnScrollChangedListener {
            refreshLayout.isEnabled = scrollView.scrollY == 0
        }
    }

    //region APIs
    private fun loadApis() {
        if (isNetworkAvailable()) {
        } else {
            activity?.openLottieDialog { loadApis(); onNetworkRetry?.invoke() }
        }
    }

}
