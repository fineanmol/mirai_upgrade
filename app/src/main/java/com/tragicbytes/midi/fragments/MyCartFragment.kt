package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.AdDetailsModel
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_show_my_banners.*
import kotlinx.android.synthetic.main.fragment_show_my_banners.refreshLayout

class MyCartFragment : BaseFragment() {

    var onNetworkRetry: (() -> Unit)? = null
    private var mAdsCompleteDetailsAdapter: RecyclerViewAdapter<AdDetailsModel.AdsCompleteDetails>? = null
    private lateinit var dbReference: DatabaseReference


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
        dbReference = FirebaseDatabase.getInstance().reference

        publishedBannersList.setVerticalLayout()
        setupAdsCompleteDetailsAdapter()

        loadApis()
        refreshLayout.setOnRefreshListener {
            loadApis()
            refreshLayout.isRefreshing=false
        }
        refreshLayout.viewTreeObserver.addOnScrollChangedListener {
            refreshLayout.isEnabled =true
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
            "https://firebasestorage.googleapis.com/v0/b/midi-trio.appspot.com/o/uploads%2FNikhil%20Nishad?alt=media&token=6a9c5b61-4c04-4aaa-9121-1fa88e38e491"
        )
        dbReference.child(
            getSharedPrefInstance().getStringValue(
                Constants.SharedPref.USER_ID
            )
        )
            .child("AdvDetails")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var myBannerList=ArrayList<AdDetailsModel.AdsCompleteDetails>()
                            dataSnapshot.children.forEach {
                                val bannerData =
                                    it.getValue(AdDetailsModel.AdsCompleteDetails::class.java)!!
                                myBannerList.add(bannerData)
                            }
                            mAdsCompleteDetailsAdapter?.addItems(myBannerList)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        rlNewestProduct.hide()
                        rcvNewestProduct.hide()
                        toast("Error Occured!")
                    }
                }

            )
    }

    private fun setupAdsCompleteDetailsAdapter() {
        mAdsCompleteDetailsAdapter = RecyclerViewAdapter(R.layout.item_banner, onBind = { view, item, position -> setBannerData(view, item) })
        //rcvNewestProduct.layoutManager = GridLayoutManager(activity,2,GridLayoutManager.HORIZONTAL, false)
        /*publishedBannersList.apply {
            layoutManager = GridLayoutManager(activity, 2, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mAdsCompleteDetailsAdapter
            rvItemAnimation()
        }*/
        publishedBannersList.adapter = mAdsCompleteDetailsAdapter

        /*mAdsCompleteDetailsAdapter?.onItemClick = { pos, view, item ->
            activity?.showBannerDetail(item)
        }*/
    }
}
