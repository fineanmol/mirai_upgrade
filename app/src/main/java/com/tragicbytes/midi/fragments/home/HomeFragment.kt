package com.tragicbytes.midi.fragments.home

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.ProductDetailActivity
import com.tragicbytes.midi.activity.WalletActivity
import com.tragicbytes.midi.adapter.HomeSliderAdapter
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.base.BaseRecyclerAdapter
import com.tragicbytes.midi.databinding.ItemCategoryBinding
import com.tragicbytes.midi.fragments.BaseFragment
import com.tragicbytes.midi.models.*
import com.tragicbytes.midi.utils.Constants.KeyIntent.DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.CONTACT
import com.tragicbytes.midi.utils.Constants.SharedPref.COPYRIGHT_TEXT
import com.tragicbytes.midi.utils.Constants.SharedPref.DEFAULT_CURRENCY
import com.tragicbytes.midi.utils.Constants.SharedPref.FACEBOOK
import com.tragicbytes.midi.utils.Constants.SharedPref.INSTAGRAM
import com.tragicbytes.midi.utils.Constants.SharedPref.PRIVACY_POLICY
import com.tragicbytes.midi.utils.Constants.SharedPref.TERM_CONDITION
import com.tragicbytes.midi.utils.Constants.SharedPref.TWITTER
import com.tragicbytes.midi.utils.Constants.SharedPref.WHATSAPP
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    //region Variables
    private var imgLayoutParams: LinearLayout.LayoutParams? = null
    private var mCategoryAdapter: BaseRecyclerAdapter<CategoryData, ItemCategoryBinding>? = null
    private var mFeaturedProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mNewArrivalProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mOfferProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mYouMayLikeProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mDealProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mSuggestedProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mRecentProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mTestimonialsAdapter: RecyclerViewAdapter<Testimonials>? = null

    private lateinit var dbReference: DatabaseReference

    var currentProgress = 0
    private val handle = Handler()


    var onNetworkRetry: (() -> Unit)? = null
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        dbReference = FirebaseDatabase.getInstance().reference
        imgLayoutParams = activity?.productLayoutParams()


        rcvNewestProduct.setVerticalLayout()
        rcvRecentSearch.setHorizontalLayout()

        setClickEventListener()

        setupNewArrivalProductAdapter()
        setupRecentProductAdapter()


        loadApis()

        refreshLayout.setOnRefreshListener {

            loadApis()
            refreshLayout.isRefreshing=false
        }
        refreshLayout.viewTreeObserver.addOnScrollChangedListener {
            if(refreshLayout != null) {
                    refreshLayout.isEnabled = scrollView.scrollY == 0
                }

        }



    }

    //region APIs
    private fun loadApis() {
        if (isNetworkAvailable()) {
            fetchUserData(dbReference, onSuccess = {
//                snackBar(it)
            },
                onFailed = {
                    snackBar(it)
                })
            listAllProducts()
//            listAllProductCategories();
            getSliders()
//            listFeaturedProducts()
        } else {
            getSliders()
            activity?.openLottieDialog { loadApis(); onNetworkRetry?.invoke() }
        }
    }

    private fun getSliders() {
//        val images = getSlideImagesFromPref()
        val images=ArrayList<SliderImagesResponse>()
        var sliderImage1=SliderImagesResponse("","",R.drawable.upload_banner_3)
        var sliderImage2=SliderImagesResponse("","",R.drawable.top_banner_2)
        var sliderImage3=SliderImagesResponse("","",R.drawable.top_banner)

        images.add(sliderImage1)
        images.add(sliderImage2)
        images.add(sliderImage3)

        val sliderImagesAdapter = HomeSliderAdapter(activity!!, images)
        homeSlider.adapter = sliderImagesAdapter
        dots.attachViewPager(homeSlider)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        sliderImagesAdapter.notifyDataSetChanged()

        if (images.isNotEmpty()) {
            rl_head.show()
        } else {
            rl_head.hide()
        }

    }

    private fun listAllProducts() {
        showProgress()
        val requestModel = RequestModel()
        if (isLoggedIn()) requestModel.user_id = getUserId()

        callApi(getRestApis(false).dashboard(requestModel), onApiSuccess = { it ->
            if (activity == null) return@callApi
            hideProgress()

//            Log.d("XXXX12",it.toString())
            getSharedPrefInstance().apply {
                removeKey(WHATSAPP)
                removeKey(FACEBOOK)
                removeKey(TWITTER)
                removeKey(INSTAGRAM)
                removeKey(CONTACT)
                removeKey(PRIVACY_POLICY)
                removeKey(TERM_CONDITION)
                removeKey(COPYRIGHT_TEXT)
                setValue(DEFAULT_CURRENCY, "&#36")

            }

            dbReference.child("AppData/imageTemplates")
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                rlNewestProduct.show()
                                rcvNewestProduct.show()
                                var myBannerList = ArrayList<ProductDataNew>()

                                dataSnapshot.children.forEach {
                                    val bannerData =
                                        it.getValue(ProductDataNew::class.java)!!
                                    if (getRecentItems().size != 0) {
                                        var recentProductUsedId =
                                            getRecentItems()[0].pro_id.toString()
                                        if ((bannerData.pro_id.toString() == recentProductUsedId)) {
                                            addToRecentProduct(bannerData)
                                            mRecentProductAdapter?.addItems(getRecentItems())
                                        }
                                    } else {
                                    }
                                    myBannerList.add(bannerData)
                                }
                                mNewArrivalProductAdapter?.addItems(myBannerList)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            if (rlNewestProduct != null) {
                                    rlNewestProduct.hide()
                                }

                            if(rcvNewestProduct != null) {
                                rcvNewestProduct.hide()
                                }

                            toast("Error Occured!")
                        }
                    }

                )



            ivBanner1.show(); ivBanner1.loadImageFromUrl("http://iqonic.design/wp-themes/woobox_api/wp-content/uploads/2020/01/167-scaled.jpg ")
            ivBanner1.onClick { /*activity?.openCustomTab("https://www.google.com/")*/ }
//
        }, onApiError = {
            //toast(it)
        }, onNetworkError = {
            toast(R.string.error_no_internet)
        })
    }


    //endregion

    //region RecyclerViews and Adapters
    private fun setupRecentProductAdapter() {
        mRecentProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })
        rcvRecentSearch.adapter = mRecentProductAdapter

        mRecentProductAdapter?.onItemClick = { pos, view, item ->
            activity?.launchActivity<ProductDetailActivity> { putExtra(DATA, item) }
        }

        mRecentProductAdapter?.addItems(getRecentItems())
        mRecentProductAdapter?.setModelSize(5)
        if (mRecentProductAdapter != null && mRecentProductAdapter!!.itemCount <= 0) rlRecentSearch.hide() else rlRecentSearch.show()
    }

    private fun setupNewArrivalProductAdapter() {
        mNewArrivalProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })

        rcvNewestProduct.apply {
            layoutManager = GridLayoutManager(activity, 2, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mNewArrivalProductAdapter
            rvItemAnimation()
        }
        rcvNewestProduct.adapter = mNewArrivalProductAdapter

        mNewArrivalProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }


    //endregion

    //region Common
    private fun getRecentItems(): ArrayList<ProductDataNew> {
        val list = recentProduct(); list.reverse(); return list
    }

    private fun setClickEventListener() {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_wallet -> {
                activity?.launchActivity<WalletActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion
}