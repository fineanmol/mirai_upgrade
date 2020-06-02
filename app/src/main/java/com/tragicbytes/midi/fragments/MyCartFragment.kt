package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.AdDetailsModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.refreshLayout
import kotlinx.android.synthetic.main.fragment_home.scrollView
import kotlinx.android.synthetic.main.fragment_show_my_banners.*

class MyCartFragment : BaseFragment() {

    var onNetworkRetry: (() -> Unit)? = null
    private var mAdsCompleteDetailsAdapter: RecyclerViewAdapter<AdDetailsModel.AdsCompleteDetails>? = null


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

        setupAdsCompleteDetailsAdapter()

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
            loadImages()
        } else {
            activity?.openLottieDialog { loadApis(); onNetworkRetry?.invoke() }
        }
    }

    private fun loadImages(){
        var adv=AdDetailsModel.AdsCompleteDetails(
            "123",
            "adDetails!!.adName",
            "adDetails!!.adDesc",
            "adDetails!!.adTagline",
            "adDetails!!.adBrandName",
            "",
            "gender",
            "ageGroup",
            "startDateVal.text.toString()",
            "endDateVal.text.toString()",
            "startTimeVal.text.toString()",
            "endTimeVal.text.toString()",
            "rangeVal.text.toString()",
            "https://i.pinimg.com/originals/6b/20/16/6b201623685e7093fe7df8970b1d26b5.jpg"
        )
        var myBannerList=ArrayList<AdDetailsModel.AdsCompleteDetails>()
        myBannerList.add(adv)
        snackBar("coooooool")
        mAdsCompleteDetailsAdapter?.addItems(myBannerList)
    }

    private fun setupAdsCompleteDetailsAdapter() {
        mAdsCompleteDetailsAdapter = RecyclerViewAdapter(R.layout.item_banner, onBind = { view, item, position -> setBannerData(view, item) })
        //rcvNewestProduct.layoutManager = GridLayoutManager(activity,2,GridLayoutManager.HORIZONTAL, false)
        publishedBannersList.apply {
            layoutManager = GridLayoutManager(activity, 2, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mAdsCompleteDetailsAdapter
            rvItemAnimation()
        }
        publishedBannersList.adapter = mAdsCompleteDetailsAdapter

        mAdsCompleteDetailsAdapter?.onItemClick = { pos, view, item ->
            activity?.showBannerDetail(item)
        }
    }
}
