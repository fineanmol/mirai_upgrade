package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import com.google.firebase.database.*
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.DashBoardActivity
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.AdDetailsModel
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.models.UserAdvertisementDetails
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_show_my_banners.*

class MyBannersFragment : BaseFragment() {


    var onNetworkRetry: (() -> Unit)? = null
    private var mAdsCompleteDetailsAdapter: RecyclerViewAdapter<SingleAdvertisementDetails>? = null
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

        btnShopNow.onClick {
            if (activity!! is DashBoardActivity) {
                (activity as DashBoardActivity).loadHomeFragment()
            }
        }
        loadApis()
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
        /*dbReference.child("UsersData/${getStoredUserDetails().userId}/userAdvertisementDetails")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var myBannerList=dataSnapshot.getValue(UserAdvertisementDetails()::class.java)!!
                            mAdsCompleteDetailsAdapter?.addItems(myBannerList.singleAdvertisementDetails)
                            publishedBannersList.visibility=View.VISIBLE
                            llNoItems.visibility=View.GONE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        if( (rlNewestProduct != null) and rlNewestProduct.isNotEmpty()) {
                            rlNewestProduct.hide()
                            rcvNewestProduct.hide()
                            toast("Error Occured!")
                            }


                    }
                }

            )*/
        mAdsCompleteDetailsAdapter?.addItems(getStoredUserDetails().userAdvertisementDetails.singleAdvertisementDetails)
        llNoItems.visibility=View.GONE
    }

    private fun setupAdsCompleteDetailsAdapter() {
        mAdsCompleteDetailsAdapter = RecyclerViewAdapter(R.layout.item_banner, onBind = { view, item, position -> setBannerData(view, item,position) })

        publishedBannersList.apply {
            adapter = mAdsCompleteDetailsAdapter
            rvItemAnimation()
        }
        publishedBannersList.adapter = mAdsCompleteDetailsAdapter

        mAdsCompleteDetailsAdapter?.onItemClick = { pos, view, item ->
            activity?.showMyBannerDetails(item)
        }
    }
}
