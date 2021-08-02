package com.tragicbytes.midi.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.LinearLayout
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.activity.ProductDetailActivity
import com.tragicbytes.midi.activity.WalletActivity
import com.tragicbytes.midi.adapter.HomeSliderAdapter
import com.tragicbytes.midi.models.SliderImagesResponse
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.dots
import kotlinx.android.synthetic.main.fragment_home.homeSlider
import kotlinx.android.synthetic.main.fragment_home.refreshLayout
import kotlinx.android.synthetic.main.fragment_home.rl_head
import kotlinx.android.synthetic.main.fragment_home.scrollView
import kotlinx.android.synthetic.main.fragment_upload_my_banner.*
import java.io.ByteArrayOutputStream


class UploadBannerFragment : BaseFragment() {
    private var imgLayoutParams: LinearLayout.LayoutParams? = null
    private var encodedImage: String? = null

    var onNetworkRetry: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_my_banner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        imgLayoutParams = activity?.productLayoutParams()

        loadApis()
        refreshLayout.setOnRefreshListener {
            loadApis()
            refreshLayout.isRefreshing=false
        }

        refreshLayout.viewTreeObserver.addOnScrollChangedListener {
           if(refreshLayout!=null)
            refreshLayout.isEnabled = scrollView.scrollY == 0
        }

        uploadBtn.onClick {
             activity?.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {

                        CropImage.activity()
//                            .setMaxCropResultSize(2000,1000)
                            .setAspectRatio(9, 16)
                            .setFixAspectRatio(true)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropWindowSize(2160,3840)
                            .setRequestedSize(2160,3840)
                            .setOutputCompressQuality(40)
                            .start(context, this@UploadBannerFragment)

                    } else {
                        activity!!.showPermissionAlert(this)
                    }
                })
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                val imageStream = activity!!.contentResolver.openInputStream(resultUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                encodedImage = encodeImage(selectedImage)

                if (encodedImage != null) {
                    snackBar("Activity need to be Run")
                    (activity!!.application as WooBoxApp).setUserUploadImageEncoded(encodedImage)
                    activity!!.launchActivity<ProductDetailActivity> {
                        putExtra(Constants.KeyIntent.USER_UPLOAD_BANNER, "TRUE")
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                if (error?.message != null){
                    snackBar(error.message!!)
                }
            }
        }else{
            /*if (data != null && data.data != null) ivProfileImage.setImageURI(data.data)
            val path: String? = ImagePicker.getImagePathFromResult(activity!!, requestCode, resultCode, data) ?: return
            val uri = FileProvider.getUriForFile(
                activity!!,
                BuildConfig.APPLICATION_ID + ".provider",
                File(path)
            )
            CropImage.activity(uri)
//                .setOutputCompressQuality(40)
                .start(activity!!)*/
        }
    }


    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)

    }


}
