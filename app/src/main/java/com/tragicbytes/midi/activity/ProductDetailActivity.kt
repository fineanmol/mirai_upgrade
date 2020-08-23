package com.tragicbytes.midi.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.razorpay.Checkout
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.adapter.PersonalizedProductImageAdapter
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.databinding.ActivityProductDetailBinding
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.SingleAdvertisementDetails
import com.tragicbytes.midi.models.UserDetailsModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.KeyIntent.DATA
import com.tragicbytes.midi.utils.Constants.KeyIntent.USER_UPLOAD_BANNER
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.dots
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_color.view.*
import kotlinx.android.synthetic.main.item_size.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt

class ProductDetailActivity : AppBaseActivity(){

    private var advGenderPref: String="Female"
    private var advAgeGroupPref: String=""
    private lateinit var mMainBinding: ActivityProductDetailBinding
    private lateinit var dbReference: DatabaseReference
    private var storageReference: StorageReference? = null
    private var mProductModel: ProductDataNew? = null
    private val myImages = ArrayList<Bitmap>()
    var i: Int = 0
    private var mColorFlag: Int = -1
    private var mSizeFlag: Int = -1
    private var mIsGenderExist: Boolean = false
    private var mIsAgeGroupExist: Boolean = false
    private var mIsStartDateExist: Boolean = false
    private var mIsEndDateExist: Boolean = false
    private var mIsRangeExist: Boolean = false
    private var mIsStartTimeExist: Boolean = false
    private var mIsEndTimeExist: Boolean = false
    private var advDetails: SingleAdvertisementDetails=SingleAdvertisementDetails()
    private var mIsAllDetailsFilled: Boolean = false
    private var colorAdapter: RecyclerViewAdapter<String>? = null
    private var ageGroupAdapter: RecyclerViewAdapter<String>? = null
    private var mMonth = 0
    private var mYear: Int = 0
    private var mDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeTransparentStatusBar()


        Checkout.preload(applicationContext)
        getSharedPrefInstance().removeKey(Constants.SharedPref.ADS_BANNER_URL)
        dbReference = FirebaseDatabase.getInstance().reference
        storageReference=FirebaseStorage.getInstance().reference
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        setToolbarWithoutBackButton(mMainBinding.toolbar)
        toolbar_layout.title = "Advertisement"
        ivBack.onClick {
            onBackPressed()
        }

        when {
            intent?.extras?.getSerializable(DATA) != null -> {
                mProductModel = intent.getSerializableExtra(DATA) as ProductDataNew
                setDetails(mProductModel!!)
            }
            intent?.extras?.get(USER_UPLOAD_BANNER) != null -> {
                var userUploadBannerModel = ProductDataNew(
                    5.00.toString(),
                    "Midi",
                    "This is custom banner upload section. Please add some more details to publish your advertisements",
                    "N/A",
                    "",
                    true,
                    "Custom Banner",
                    "",
                    99999,
                    "1000",
                    "500"
                )
                mProductModel = userUploadBannerModel
                setDetails(mProductModel!!)
            }
            else -> {
                toast(R.string.error_something_went_wrong)
                finish()
            }
        }

        btnAddCard.onClick {
            if (isLoggedIn()) {
                launchActivity<AdvertisementFormActivity>(21)
            } else {
                launchActivity<SignInUpActivity>()
            }
        }


        /*try {
            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val formatted = current.format(formatter)
            startDateVal.text =formatted.toString()
        } catch (e: Exception) {
            e.message?.let { snackBarError(it) }
        }*/
    }



    private fun setDetails(mProductModel: ProductDataNew) {
        mMainBinding.model = mProductModel

        when {
            mProductModel.sale_price!!.isNotEmpty() -> tvPrice.text =
                mProductModel.sale_price.currencyFormat()
            else -> tvPrice.text = mProductModel.price.toString().currencyFormat()
        }
        tvItemProductOriginalPrice.text = mProductModel.regular_price?.currencyFormat()
        tvItemProductOriginalPrice.applyStrike()
        if (mProductModel.regular_price != null && mProductModel.regular_price.isNotEmpty()) {
            val mrp = mProductModel.regular_price.toDouble()
            var discountPrice: Double = when {
                mProductModel.sale_price.isNotEmpty() -> mProductModel.sale_price.toDouble()
                else -> mProductModel.price!!.toDouble()
            }
            if (mrp != 0.0) {
                val sub = mrp - discountPrice
                val discount = round(((sub * 100) / mrp), 2)
                if (discount == 0.0) {
                    tvItemProductDiscount.hide()
                } else {
                    tvItemProductDiscount.show()
                    tvItemProductDiscount.text = "${discount.toInt()}% Off"
                }
            } else {
                tvItemProductDiscount.hide()
            }

        } else {
            tvItemProductDiscount.hide()
        }
        toolbar_layout.setCollapsedTitleTypeface(fontSemiBold())
        toolbar_layout.setExpandedTitleTypeface(fontSemiBold())
        toolbar_layout.title = mProductModel.name
        if (mProductModel.name.toString() == "Custom Banner") btnAddCard.hide() else btnAddCard.show()

        intHeaderView()



        /*submitForPaymentBtn.onClick {

            if (validateAllValue()) {
                updateDbValues()
            }

        }*/

        mIsRangeExist = true


        val sdf = SimpleDateFormat("dd-M-yyyy")
        val currentDate = sdf.format(Date())
        startDateVal.text=currentDate

        startDateVal.onClick {
            val datePickerDialog = DatePickerDialog(
                this@ProductDetailActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    startDateVal.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
            mIsStartDateExist = !startDateVal.text.isNullOrEmpty()
        }

        endDateVal.onClick {
            val datePickerDialog = DatePickerDialog(
                this@ProductDetailActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    endDateVal.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
            mIsEndDateExist = !endDateVal.text.isNullOrEmpty()
        }

        val sdf2 = SimpleDateFormat("HH:mm")
        val currentTime = sdf2.format(Date())
        startTimeVal.text=currentTime
        startTimeVal.onClick {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                startTimeVal.text = "$hour:$minute"
            }
            TimePickerDialog(
                this@ProductDetailActivity,
                timeSetListener,
                0,0,
                true
            ).show()
            mIsStartTimeExist = true
        }

        endTimeVal.onClick {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                endTimeVal.text = "$hour:$minute"
            }
            TimePickerDialog(
                this@ProductDetailActivity,
                timeSetListener,
                0,0,
                true
            ).show()
            mIsEndTimeExist = true
        }

        bannerUpload.onClick {
            if (validateAllValue()) {
                val dialog = getAlertDialog(
                    "While your Banner is processing, Please continue with next details",
                    "Information",
                    onPositiveClick = { dialog, i ->


                            updateDbValues()

                    },
                    onNegativeClick = { dialog, i ->
                        dialog.dismiss()
                    })
                dialog.show()

            }

        }

        selectLocation.onClick { onBackPressed() }

    }

    private fun validateAllValue(): Boolean {
        if (!mIsGenderExist) {
            snackBar("Gender Required ", Snackbar.LENGTH_SHORT)
            return false
        } else if (!mIsAgeGroupExist) {
            snackBar("Age Group Required", Snackbar.LENGTH_SHORT)
            return false
        } else if (!mIsAgeGroupExist) {
            snackBar("Start Date Required", Snackbar.LENGTH_SHORT)
            return false
        } else if (!mIsEndDateExist) {
            snackBar("End Date Required", Snackbar.LENGTH_SHORT)
            return false
        } else if (!mIsStartTimeExist) {
            snackBar("Start Time Required", Snackbar.LENGTH_SHORT)
            return false
        } else if (!mIsEndTimeExist) {
            snackBar("End Time Required", Snackbar.LENGTH_SHORT)
            return false
        } /*else if (rangeVal.textToString().isEmpty()) {
            snackBar("Range Required", Snackbar.LENGTH_SHORT)
            return false
        }*/ else {
            mIsAllDetailsFilled = true
            return true
        }

    }

    fun round(value: Double, places: Int): Double {
        var double = value
        val factor = 10.0.pow(places.toDouble()).toLong()
        double *= factor
        val tmp = double.roundToInt()
        return tmp.toDouble() / factor
    }

    private fun intHeaderView() {
        var advDetails = SingleAdvertisementDetails()
        advDetails.advName="Test Name"
        advDetails.advDescription="Test adv description"
        advDetails.advTagline="#ourTagLine"
        advDetails.advBrandName="Test Brand"
        showProgress(true)

        if (mProductModel!!.full.toString().isNotEmpty()) {
            fetchImageAsync(mProductModel!!.full.toString()) {
                if (it != null) {
                    val personalizedBannerBitmap =
                        drawTextToBitmap(this, it, advDetails)!!
                    myImages.add(personalizedBannerBitmap)
                    val imageAdapter = PersonalizedProductImageAdapter(myImages)
                    productViewPager.adapter = null
                    productViewPager.adapter = imageAdapter
                    productViewPager.adapter?.notifyDataSetChanged()
                    dots.attachViewPager(productViewPager)
                    dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
                    showProgress(false)
                }
            }
        } else {
            if(intent?.extras?.getString(USER_UPLOAD_BANNER)=="TRUE"){
                val encodeByte: ByteArray =
                    Base64.decode((this.application as WooBoxApp).getUserUploadImageEncoded(), Base64.DEFAULT)
                val personalizedBannerBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)

                myImages.add(personalizedBannerBitmap)
                val imageAdapter = PersonalizedProductImageAdapter(myImages)
                productViewPager.adapter = null
                productViewPager.adapter = imageAdapter
                productViewPager.adapter?.notifyDataSetChanged()
                dots.attachViewPager(productViewPager)
                dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
            }
            else{
                snackBar("Something went wrong!Please retry.")
            }
            showProgress(false)
        }

        setDescription()
//        setMoreInfo()
        tvItemProductOriginalPrice.applyStrike()
    }


    private fun setDescription() {
        bindData()
    }

    private fun bindData() {
        txtDescription.setText("short description of the product. We can add as many short description here according to product id")
        val genderList = ArrayList<String>()
        val genders = listOf("Male", "Female", "Not Specified", "All")
        val ageGroupList = ArrayList<String>()
        val ageGroups = listOf("13-17", "18-24", "25-34", "35-44","45+")

        if (genders.isNotEmpty()) {

//            val colors = mProductModel?.color?.split(",")


            genders.forEachIndexed { _, s ->
                genderList.add(s.trim())
            }
            colorAdapter =
                RecyclerViewAdapter(R.layout.item_color, onBind = { view, item, position ->
                    if (item.isNotEmpty()) {
                        try {
                            if (item.contains("#")) {
                                view.viewColor.show()
                                view.tvColor.hide()
                                view.viewColor.setStrokedBackground(
                                    Color.parseColor(item.trim()),
                                    strokeColor = color(R.color.white)
                                )
                                view.ivColorChecked.setStrokedBackground(
                                    Color.parseColor(item.trim()),
                                    strokeColor = color(R.color.white)
                                )
                                when (position) {
                                    mColorFlag -> {
                                        view.viewColor.hide()
                                        view.ivColorChecked.show()
                                    }
                                    else -> {
                                        view.viewColor.show()
                                        view.ivColorChecked.hide()
                                    }
                                }
                            } else {
                                view.viewColor.hide()
                                view.tvColor.show()
                                view.tvColor.text = item
                                view.tvColor.apply {
                                    when (position) {
                                        mColorFlag -> {
                                            setTextColor(color(R.color.commonColorWhite))
                                            setStrokedBackground(color(R.color.colorPrimary))
                                        }
                                        else -> {
                                            setTextColor(color(R.color.textColorPrimary))
                                            setStrokedBackground(
                                                0,
                                                strokeColor = color(R.color.view_color)
                                            )
                                        }
                                    }

                                }

                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            colorAdapter?.onItemClick = { pos, view, item ->
                mColorFlag = pos
                advGenderPref= genderList[pos]
                mIsGenderExist = true && advGenderPref.isNotEmpty()
                colorAdapter?.notifyDataSetChanged()
                getSelectedColors()
            }
            colorAdapter?.addItems(genderList)
            rvColors.setHorizontalLayout()
            rvColors.adapter = colorAdapter
        } else {
            tvColors.show()
            rvColors.show()
        }

        if (ageGroups.isNotEmpty()) {

            ageGroups.forEachIndexed { i, s -> ageGroupList.add(s.trim()) }

            ageGroupAdapter =
                RecyclerViewAdapter(R.layout.item_size, onBind = { view, item, position ->
                    if (item.isNotEmpty()) {
                        view.llSize.show()
                        view.ivSizeChecked.text = item
                        /* if (mIsFirstTimeSize && mProductModel?.size == item) {
                             mIsFirstTimeSize = false
                             mSizeFlag = position
                         }*/
                        view.ivSizeChecked.apply {
                            when (position) {
                                mSizeFlag -> {
                                    setTextColor(color(R.color.commonColorWhite))
                                    setStrokedBackground(color(R.color.colorPrimary))
                                }
                                else -> {
                                    setTextColor(color(R.color.textColorPrimary))
                                    setStrokedBackground(
                                        0,
                                        strokeColor = color(R.color.view_color)
                                    )
                                }
                            }

                        }
                    } else {
                        view.llSize.hide()
                    }
                })
            ageGroupAdapter?.onItemClick = { pos, view, item ->
                mSizeFlag = pos
                advAgeGroupPref = ageGroupList[pos]
                mIsAgeGroupExist = true && advAgeGroupPref.isNotEmpty()
                ageGroupAdapter?.notifyDataSetChanged()
                getSelectedSize()
            }

            ageGroupAdapter?.addItems(ageGroupList)
            rvSize.setHorizontalLayout()
            rvSize.adapter = ageGroupAdapter
        } else {
            tvSize.hide()
            rvSize.hide()
        }
    }

    private fun getSelectedColors(): String? {
        return when (mColorFlag) {
            -1 -> null
            else -> colorAdapter?.getModel()!![mColorFlag]
        }
    }

    private fun getSelectedSize(): String? {
        return when (mSizeFlag) {
            -1 -> null
            else -> ageGroupAdapter?.getModel()!![mSizeFlag]
        }
    }

    private fun changeFavIcon(
        drawable: Int,
        backgroundColor: Int,
        iconTint: Int = R.color.textColorSecondary
    ) {
        ivFavourite.setImageResource(drawable)
        ivFavourite.applyColorFilter(color(iconTint))
        ivFavourite.changeBackgroundTint(color(backgroundColor))
    }

    override fun onResume() {
        super.onResume()
        if (!isAdShown) {
            showInterstitialAd()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check that it is the SecondActivity with an OK result
        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) { // Get String data from Intent
                showProgress(true)
                val returnString = data?.getSerializableExtra("AdvFormData")
                // Set text view with string
                advDetails = returnString as SingleAdvertisementDetails
                myImages.clear()
                fetchImageAsync(mProductModel!!.full.toString()) {
                    if (it != null) {
                        val personalizedBannerBitmap =
                            drawTextToBitmap(this, it, advDetails)!!
                        myImages.add(personalizedBannerBitmap)
                        val imageAdapter = PersonalizedProductImageAdapter(myImages)
                        productViewPager.adapter = null
                        productViewPager.adapter = imageAdapter
                        productViewPager.adapter?.notifyDataSetChanged()
                        dots.attachViewPager(productViewPager)
                        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
                        showProgress(false)
                    }
                }
            }
        }
    }

    private fun updateDbValues() {
        snackBar("Details filled")
        showProgress(true)

        try {
            /** BannerImageToDB*/
            this@ProductDetailActivity.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {
                        val adsDetails=SingleAdvertisementDetails()
                        showProgress(true)
                        this@ProductDetailActivity.saveLogoImageToStorage(this@ProductDetailActivity,
                            storageReference!!,
                            myImages[0],
                            onSuccess = { bannerImageUrl ->
                                /*showProgress(false)
                                var startDate = startDateVal.text.toString()

                                if(intent.getSerializableExtra(DATA)!=null){
                                    mProductModel = intent.getSerializableExtra(DATA) as ProductDataNew
                                }

                                //region When Create Ads
                                if (mProductModel!!.name == "Custom Banner") {
                                    *//*val adsDetails = AdDetailsModel.AdsCompleteDetails(
                                        adDetails,
                                        adsGender,
                                        adsAgeGroup,
                                        startDate,
                                        endDateVal.text.toString(),
                                        startTimeVal.text.toString(),
                                        endTimeVal.text.toString(),
                                        *//**//*"₹" + rangeVal.textToString(),*//**//*
                                        bannerImageUrl
                                    )
                                    saveBannerDetailsToDB(adsDetails)*//*
                                } else {
                                    adsDetails.advId=generateString()
                                    getSharedPrefInstance().setValue(Constants.SharedPref.CURRENT_ADV_ID, adsDetails.advId)
                                    adsDetails.advAgePref.add(advAgeGroupPref)
                                    adsDetails.advGenderPref=advGenderPref
                                    adsDetails.advBannerUrl=bannerImageUrl
                                    adsDetails.advBrandName= advDetails.advBrandName
                                    adsDetails.advDescription=advDetails.advDescription
                                    adsDetails.advTagline=advDetails.advTagline
                                    adsDetails.startFrom=getTimeStamp(startDateVal.text.toString(),startTimeVal.text.toString())
                                    adsDetails.endOn=getTimeStamp(endDateVal.text.toString(),endTimeVal.text.toString())
                                    *//*adsDetails.advRange = rangeVal.text.toString()*//*
                                    var localUserDetails=getStoredUserDetails()
                                    var advDetails=localUserDetails.userAdvertisementDetails.singleAdvertisementDetails
                                    advDetails.add(adsDetails)
                                    localUserDetails.userAdvertisementDetails.singleAdvertisementDetails=advDetails
                                    saveBannerDetailsToDB(localUserDetails,adsDetails)
                                }*/
                                Log.d("xxx","upload-success")
                            },onUploading = {
                                /*if(it>5F){
                                    determinate.resetIcon()
                                    determinate.visibility=View.VISIBLE
                                    determinate.setProgress(5F)
                                }
                                determinate.showShadow(true)
                                determinate.showProgress(true)
                                determinate.setProgress(it)*/
                                Log.d("xxx","uploading${it}")
                                adsDetails.advId=generateString()
                                getSharedPrefInstance().setValue(Constants.SharedPref.CURRENT_ADV_ID, adsDetails.advId)
                                adsDetails.advAgePref.add(advAgeGroupPref)
                                adsDetails.advGenderPref=advGenderPref
//                                adsDetails.advBannerUrl=bannerImageUrl
                                adsDetails.advBrandName= advDetails.advBrandName
                                adsDetails.advDescription=advDetails.advDescription
                                adsDetails.advTagline=advDetails.advTagline
                                adsDetails.startFrom=getTimeStamp(startDateVal.text.toString(),startTimeVal.text.toString())
                                adsDetails.endOn=getTimeStamp(endDateVal.text.toString(),endTimeVal.text.toString())
                                /*adsDetails.advRange = rangeVal.text.toString()*/
                                var localUserDetails=getStoredUserDetails()
                                var advDetails=localUserDetails.userAdvertisementDetails.singleAdvertisementDetails
                                advDetails.add(adsDetails)
                                localUserDetails.userAdvertisementDetails.singleAdvertisementDetails=advDetails
                                snackBar("Ads Details Saved")
                                showProgress(false)
                                launchActivity<LocationBasedScreensActivity> {
                                    putExtra("ongoing_adv", adsDetails)
                                }
                            },
                            onFailed = {
                                Log.d("xxx","upload-failed")
                            }
                        )
                    } else {
                        showProgress(false)
                        this@ProductDetailActivity.showPermissionAlert(
                            window.decorView.findViewById(android.R.id.content)
                        )
                    }
                })

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun getTimeStamp(startDate: String, startTime: String): String {
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date = formatter.parse("$startDate $startTime") as Date
        return date.time.toString()
    }

    private fun saveBannerDetailsToDB(
        localUserDetails: UserDetailsModel,
        adsDetails: SingleAdvertisementDetails
    ) {
        /*dbReference.child(
            "UsersData/"+getStoredUserDetails().userId
        )
            .setValue(localUserDetails)
            .addOnSuccessListener {
                snackBar("Ads Saved")
                showProgress(false)
                launchActivity<LocationBasedScreensActivity>{
                    putExtra("ongoing_adv",adsDetails)
                }
            }
            .addOnFailureListener {
                snackBar(it.message.toString())
                showProgress(false)

            }*/

    }

    private fun generateString(): String? {
        val uuid: String = UUID.randomUUID().toString()
        return uuid.replace("-".toRegex(), "")
    }
    //endregion
}
