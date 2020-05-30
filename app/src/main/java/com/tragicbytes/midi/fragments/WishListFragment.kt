package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.google.gson.Gson
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.SearchActivity
import com.tragicbytes.midi.adapter.HomeSliderAdapter
import com.tragicbytes.midi.base.BaseRecyclerAdapter
import com.tragicbytes.midi.databinding.ItemWishlistBinding
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.models.WishListData
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.KeyIntent.PRODUCT_ID
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_wishlist.*
import kotlinx.android.synthetic.main.layout_nodata.*

class WishListFragment : BaseFragment() {
    private var imgLayoutParams: LinearLayout.LayoutParams? = null

    var onNetworkRetry: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_my_banner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgLayoutParams = activity?.productLayoutParams()

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
            getSliders()
        } else {
            getSliders()
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
            getSharedPrefInstance().setValue(Constants.SharedPref.SLIDER_IMAGES_DATA, Gson().toJson(res))
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
}
