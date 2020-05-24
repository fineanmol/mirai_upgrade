package com.tragicbytes.midi.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.google.gson.Gson
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.DashBoardActivity
import com.tragicbytes.midi.activity.ProductDetailActivity
import com.tragicbytes.midi.activity.SearchActivity
import com.tragicbytes.midi.adapter.HomeSliderAdapter
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.base.BaseRecyclerAdapter
import com.tragicbytes.midi.databinding.ItemCategoryBinding
import com.tragicbytes.midi.fragments.BaseFragment
import com.tragicbytes.midi.models.*
import com.tragicbytes.midi.utils.Constants.KeyIntent.DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.CATEGORY_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.CONTACT
import com.tragicbytes.midi.utils.Constants.SharedPref.COPYRIGHT_TEXT
import com.tragicbytes.midi.utils.Constants.SharedPref.DEFAULT_CURRENCY
import com.tragicbytes.midi.utils.Constants.SharedPref.FACEBOOK
import com.tragicbytes.midi.utils.Constants.SharedPref.INSTAGRAM
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_ORDER_COUNT
import com.tragicbytes.midi.utils.Constants.SharedPref.PRIVACY_POLICY
import com.tragicbytes.midi.utils.Constants.SharedPref.SLIDER_IMAGES_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.TERM_CONDITION
import com.tragicbytes.midi.utils.Constants.SharedPref.THEME_COLOR
import com.tragicbytes.midi.utils.Constants.SharedPref.TWITTER
import com.tragicbytes.midi.utils.Constants.SharedPref.WHATSAPP
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_testimonial.view.*


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

    var onNetworkRetry: (() -> Unit)? = null
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        imgLayoutParams = activity?.productLayoutParams()

     rcvFeaturedProducts.setHorizontalLayout(); rcvCategory.setHorizontalLayout()
        rcvTestimonials.setHorizontalLayout()
        mCategoryAdapter = activity!!.getCategoryAdapter(); rcvCategory.adapter = mCategoryAdapter

        setClickEventListener()

        setupRecentProductAdapter(); setupNewArrivalProductAdapter(); setupFeaturedProductAdapter();setTestimonialAdapter()

        setupOfferProductAdapter(); setupSuggestedProductAdapter(); setupYouMayLikeProductAdapter(); setupDealProductAdapter()

        loadApis()
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
            listAllProducts(); listAllProductCategories(); getSliders();listFeaturedProducts()
        } else {
            listAllProductCategories(); getSliders()
            activity?.openLottieDialog { loadApis(); onNetworkRetry?.invoke() }
        }
    }

    private fun getSliders() {
        val images = getSlideImagesFromPref()
        val sliderImagesAdapter = HomeSliderAdapter(activity!!, images)
        homeSlider.adapter = sliderImagesAdapter
        dots.attachViewPager(homeSlider)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        if (images.isNotEmpty()) {
            rl_head.show()
        } else {
            rl_head.hide()
        }

        callApi(getRestApis(false).getSliderImages(), onApiSuccess = { res ->
            if (activity == null) return@callApi
            getSharedPrefInstance().setValue(SLIDER_IMAGES_DATA, Gson().toJson(res))
            images.clear()
            images.addAll(getSlideImagesFromPref())
            dots.attachViewPager(homeSlider)
            dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
            sliderImagesAdapter.notifyDataSetChanged()
            if (images.isNotEmpty()) {
                rl_head.show()
            } else {
                rl_head.hide()
            }
        }, onApiError = {
            if (activity == null) return@callApi
            rl_head.hide()
        }, onNetworkError = {
            if (activity == null) return@callApi
            activity?.noInternetSnackBar()
            rl_head.hide()
        })
    }

    private fun listAllProducts() {
        showProgress()
        val requestModel = RequestModel()
        if (isLoggedIn()) requestModel.user_id = getUserId()

        callApi(getRestApis(false).dashboard(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            hideProgress()

            getSharedPrefInstance().apply {
                removeKey(WHATSAPP)
                removeKey(FACEBOOK)
                removeKey(TWITTER)
                removeKey(INSTAGRAM)
                removeKey(CONTACT)
                removeKey(PRIVACY_POLICY)
                removeKey(TERM_CONDITION)
                removeKey(COPYRIGHT_TEXT)
                setValue(DEFAULT_CURRENCY, it.currency_symbol.currency_symbol)
                setValue(KEY_ORDER_COUNT, it.total_order)
                setValue(THEME_COLOR, it.theme_color)
                setValue(WHATSAPP, it.social_link?.whatsapp)
                setValue(FACEBOOK, it.social_link?.facebook)
                setValue(TWITTER, it.social_link?.twitter)
                setValue(INSTAGRAM, it.social_link?.instagram)
                setValue(CONTACT, it.social_link?.contact)
                setValue(PRIVACY_POLICY, it.social_link?.privacy_policy)
                setValue(TERM_CONDITION, it.social_link?.term_condition)
                setValue(COPYRIGHT_TEXT, it.social_link?.copyright_text)
            }

            if (it.newest.isEmpty()) {

            } else {

                mNewArrivalProductAdapter?.addItems(it.newest)
            }
            if (it.featured.isEmpty()) {
                rlFeatured.hide()
                rcvFeaturedProducts.hide()
            } else {
                rlFeatured.show()
                rcvFeaturedProducts.show()
                mFeaturedProductAdapter?.addItems(it.featured)
            }
            if (it.testimonials.isEmpty()) {
                rlTestimonials.hide()
                rcvTestimonials.hide()
            } else {
                rlTestimonials.show()
                rcvTestimonials.show()
                mTestimonialsAdapter?.addItems(it.testimonials)
            }

            if (it.deal_product.isEmpty()) {

            } else {

                mDealProductAdapter?.addItems(it.deal_product)
            }
            if (it.you_may_like.isEmpty()) {

            } else {

                mYouMayLikeProductAdapter?.addItems(it.newest)
            }
            if (it.offer.isEmpty()) {

            } else {

                mOfferProductAdapter?.addItems(it.offer)
            }
            if (it.suggested_product.isEmpty()) {

            } else {

                mSuggestedProductAdapter?.addItems(it.suggested_product)
            }

            if (it.banner_1 != null && it.banner_1.url.isNotEmpty()) {

            } else {

            }
            if (it.banner_2 != null && it.banner_2.url.isNotEmpty()) {

            } else {

            }
            if (it.banner_3 != null && it.banner_3.url.isNotEmpty()) {
                ivBanner3.show(); ivBanner3.loadImageFromUrl(it.banner_3.image); ivBanner3.onClick { activity?.openCustomTab(it.banner_3.url) }
            } else {
                ivBanner3.hide()
            }
        }, onApiError = {
            //toast(it)
        }, onNetworkError = {
            toast(R.string.error_no_internet)
        })
    }

    private fun listFeaturedProducts() {
        showProgress()
        val requestModel = FilterProductRequest()
        callApi(getRestApis(false).getFeaturedProducts(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            hideProgress()
            if (it.isEmpty()) {
                rlFeatured.hide()
                rcvFeaturedProducts.hide()
            } else {
                rlFeatured.show()
                rcvFeaturedProducts.show()
                mFeaturedProductAdapter?.addItems(it)
                mFeaturedProductAdapter?.setModelSize(5)
            }
        }, onApiError = {
            if (activity == null) return@callApi
            hideProgress()
        }, onNetworkError = {
            if (activity == null) return@callApi
            hideProgress()
            activity?.noInternetSnackBar()
        })
    }

    private fun listAllProductCategories() {
        val categories = getCategoryDataFromPref()
        if (categories.isNotEmpty()) {
            mCategoryAdapter?.addItems(categories)
            if (activity != null) (activity as DashBoardActivity).setDrawerCategory(categories)
        }

        callApi(getRestApis(false).getProductCategories(), onApiSuccess = {
            if (activity == null) return@callApi
            getSharedPrefInstance().setValue(CATEGORY_DATA, Gson().toJson(it))
            mCategoryAdapter?.addItems(it)

            if (activity != null) (activity as DashBoardActivity).setDrawerCategory(it)
        }, onApiError = {
            if (activity == null) return@callApi
        }, onNetworkError = {
            if (activity == null) return@callApi
            activity?.noInternetSnackBar()
        })
    }
    //endregion

    //region RecyclerViews and Adapters
    private fun setupRecentProductAdapter() {
        mRecentProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })


        mRecentProductAdapter?.onItemClick = { pos, view, item ->
            activity?.launchActivity<ProductDetailActivity> { putExtra(DATA, item) }
        }

        mRecentProductAdapter?.addItems(getRecentItems())
        mRecentProductAdapter?.setModelSize(5)


    }

    private fun setupNewArrivalProductAdapter() {
        mNewArrivalProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })
       /* rcvNewestProduct.adapter = mNewArrivalProductAdapter*/

        mNewArrivalProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())

        }
    }

    private fun setupFeaturedProductAdapter() {
        mFeaturedProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })
        rcvFeaturedProducts.adapter = mFeaturedProductAdapter

        mFeaturedProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())

        }
    }

    private fun setupOfferProductAdapter() {
        mOfferProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })

        mOfferProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
        }
    }

    private fun setupSuggestedProductAdapter() {
        mSuggestedProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })

        mSuggestedProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
        }
    }

    private fun setupYouMayLikeProductAdapter() {
        mYouMayLikeProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })


        mYouMayLikeProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())

        }
    }

    private fun setupDealProductAdapter() {
        mDealProductAdapter = RecyclerViewAdapter(R.layout.item_product_new, onBind = { view, item, position -> setProductItem(view, item) })


        mDealProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
     }
    }

    private fun setTestimonialAdapter() {
        mTestimonialsAdapter = RecyclerViewAdapter(R.layout.item_testimonial, onBind = { view, item, position ->
            view.ivAuthor.loadImageFromUrl(item.image!!)
            view.tvName.text = item.name
            view.tvDesignation.text = ""
            view.tvDesignation.text = item.designation
            if (item.company != null && item.company.isNotEmpty()) {
                view.tvDesignation.append(", " + item.company)
            }
            view.tvDescription.text = "\"" + item.message + "\""
        })
        rcvTestimonials.adapter = mTestimonialsAdapter
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
            R.id.action_search -> {
                activity?.launchActivity<SearchActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion

}