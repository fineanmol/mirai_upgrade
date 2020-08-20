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
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.razorpay.Checkout
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.adapter.PersonalizedProductImageAdapter
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.databinding.ActivityProductDetailBinding
import com.tragicbytes.midi.models.*
import com.tragicbytes.midi.utils.Constants.KeyIntent.DATA
import com.tragicbytes.midi.utils.Constants.KeyIntent.USER_UPLOAD_BANNER
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.item_color.view.*
import kotlinx.android.synthetic.main.item_size.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt

class ProductDetailActivity : AppBaseActivity(){

    private lateinit var mMainBinding: ActivityProductDetailBinding
    private lateinit var dbReference: DatabaseReference
    private var storageReference: StorageReference? = null
    private var mProductModel: ProductDataNew? = null
    private var mAdsModel: AdsMoreDetails? = null
    private var mProductId = 0
    private var isAddedToCart: Boolean = false
    private val mImages = ArrayList<String>()
    private val myImages = ArrayList<Bitmap>()
    var i: Int = 0
    private var adsGender: String = ""
    private var adsAgeGroup: String = ""
    private var mIsInWishList = false
    private var mColorFlag: Int = -1
    private var mSizeFlag: Int = -1
    private var mQuntity: String = "1"
    private var mIsGenderExist: Boolean = false
    private var mIsAgeGroupExist: Boolean = false
    private var mIsStartDateExist: Boolean = false
    private var mIsEndDateExist: Boolean = false
    private var mIsRangeExist: Boolean = false
    private var mIsStartTimeExist: Boolean = false
    private var mIsEndTimeExist: Boolean = false
    private var adDetails: AdDetailsModel.AdDetails? = null
    private var mIsAllDetailsFilled: Boolean = false
    private var mIsFirstTimeSize = true
    private var colorAdapter: RecyclerViewAdapter<String>? = null
    private var ageGroupAdapter: RecyclerViewAdapter<String>? = null
    private var mMonth = 0
    private var mYear: Int = 0
    private var mDay: Int = 0
    private var advCount:String?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeTransparentStatusBar()


        Checkout.preload(applicationContext)
        dbReference = FirebaseDatabase.getInstance().reference
        storageReference=FirebaseStorage.getInstance().reference
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        setToolbarWithoutBackButton(mMainBinding.toolbar)
        toolbar_layout.title = "Advertisement"
        ivBack.onClick {
            onBackPressed()
        }

        dbReference.child(getStoredUserDetails().userId)
            .child("UserAdvertisementDetails").orderByKey().limitToLast(1).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        p0.children.forEach {
                            advCount= (it.key!!.toInt()+1).toString()
                        }
                    }
                    else{
                        advCount=0.toString()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    snackBar(p0.message)
                }
            })


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
        setCartCountFromPref()


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


    private fun setCartCountFromPref() {

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



        startDateVal.onClick {
//            getSharedPrefInstance().removeKey(Constants.AdvTimeDetails.Start_Date)
            val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Calendar.getInstance()
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            mYear = c[Calendar.YEAR]

            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@ProductDetailActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    startDateVal.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
            val coMonth = c[Calendar.MONTH]
            val coDay = c[Calendar.DAY_OF_MONTH]

//            getSharedPrefInstance().setValue(Constants.AdvTimeDetails.Start_Date,startDateVal.text.toString())
            mIsStartDateExist = !startDateVal.text.isNullOrEmpty()
        }

        endDateVal.onClick {

            val c = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Calendar.getInstance()
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@ProductDetailActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    endDateVal.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

            datePickerDialog.show()
            val coMonth = c[Calendar.MONTH]
            val coDay = c[Calendar.DAY_OF_MONTH]
//            getSharedPrefInstance().setValue(Constants.AdvTimeDetails.End_Date,endDateVal.text.toString())
            mIsEndDateExist = !endDateVal.text.isNullOrEmpty()
        }

        startTimeVal.onClick {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTimeVal.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this@ProductDetailActivity,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
//            getSharedPrefInstance().setValue(Constants.AdvTimeDetails.Start_Time,startTimeVal.text.toString())

            mIsStartTimeExist = true
        }

        endTimeVal.onClick {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                endTimeVal.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this@ProductDetailActivity,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
//            getSharedPrefInstance().setValue(Constants.AdvTimeDetails.End_Time,endTimeVal.text.toString())
            mIsEndTimeExist = true
        }

        bannerUpload.onClick {
            showProgress(true)
            myImages[0]
            this@ProductDetailActivity.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {
                        showProgress(true)
                        this@ProductDetailActivity.saveLogoImageToStorage(this@ProductDetailActivity,
                            dbReference,
                            storageReference!!,
                            myImages[0],
                            onSuccess = {
                                showProgress(false)
                                bannerUpload.text= "Continue"
                                bannerUpload.setCompoundDrawables(null,null,null,null)
                            }
                        )
                    } else {
                        showProgress(false)
                        snackBarError("Upload Failed")
                        bannerUpload.text= "Upload"
                        //    this@ProductDetailActivity.showPermissionAlert(this)
                    }
                })


            if (validateAllValue()) {
                updateDbValues()
            }
            /*val value = getSharedPrefInstance().getStringValue(Constants.AdvTimeDetails.Start_Date)
            snackBar(value)*/

        }

        selectLocation.onClick { launchActivity<LocationBasedScreensActivity>() }



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
        var adDetails =
            AdDetailsModel.AdDetails(
                "Test Name",
                "Test adv description",
                "#ourTagLine",
                "Test Brand",
                ""
            )
        showProgress(true)

        if (mProductModel!!.full.toString().isNotEmpty()) {
            fetchImageAsync(mProductModel!!.full.toString()) {
                if (it != null) {
                    val personalizedBannerBitmap =
                        drawTextToBitmap(this, it, adDetails)!!
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
                adsGender = genderList[pos]
                mIsGenderExist = true && adsGender.isNotEmpty()
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
                                    //          adsGender= genderList[position]
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
                adsAgeGroup = ageGroupList[pos]
                mIsAgeGroupExist = true && adsAgeGroup.isNotEmpty()
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
                adDetails = returnString as AdDetailsModel.AdDetails
                myImages.clear()
                fetchImageAsync(mProductModel!!.full.toString()) {
                    if (it != null) {
                        val personalizedBannerBitmap =
                            drawTextToBitmap(this, it, adDetails!!)!!
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
                        showProgress(true)
                        this@ProductDetailActivity.saveLogoImageToStorage(this@ProductDetailActivity,
                            dbReference,
                            storageReference!!,
                            myImages[0],
                            onSuccess = { bannerImageUrl ->
                                showProgress(false)
                                var startDate = startDateVal.text.toString()
//            if (startDate == "Today") startDate = LocalDate.now().toString()

                                if(intent.getSerializableExtra(DATA)!=null){
                                    mProductModel = intent.getSerializableExtra(DATA) as ProductDataNew
                                }

                                //region When Create Ads
                                if (mProductModel!!.name != "Custom Banner") {
                                    if (adDetails != null) {
                                        /*val adsDetails = AdDetailsModel.AdsCompleteDetails(
                                            adDetails,
                                            adsGender,
                                            adsAgeGroup,
                                            startDate,
                                            endDateVal.text.toString(),
                                            startTimeVal.text.toString(),
                                            endTimeVal.text.toString(),
                                            *//*"₹" + rangeVal.textToString(),*//*
                                            bannerImageUrl
                                        )
                                        saveBannerDetailsToDB(adsDetails)*/
                                    } else {
                                        snackBar("Please Create ads First")
                                    }
                                } else {
                                    /*val adsDetails = AdDetailsModel.AdsCompleteDetails(
                                        adDetails,
                                        adsGender,
                                        adsAgeGroup,
                                        startDate,
                                        endDateVal.text.toString(),
                                        startTimeVal.text.toString(),
                                        endTimeVal.text.toString(),
                                     *//*   "₹" + rangeVal.textToString(),*//*
                                        bannerImageUrl
                                    )*/
                                    val adsDetails=SingleAdvertisementDetails()
                                    adsDetails.advAgePref= ArrayList()
                                    adsDetails.advBannerUrl=bannerImageUrl
                                    adsDetails.advBrandName= adDetails!!.adBrandName
                                    adsDetails.advDescription=adDetails!!.adDesc
                                    adsDetails.advTagline=adDetails!!.adTagline
                                    adsDetails.startFrom=getTimeStamp(startDateVal.text.toString(),startTimeVal.text.toString())
                                    adsDetails.endOn=getTimeStamp(endDateVal.text.toString(),endTimeVal.text.toString())
                                    var localUserDetails=getStoredUserDetails()
                                    var advDetails=localUserDetails.userAdvertisementDetails.singleAdvertisementDetails
                                    advDetails.add(adsDetails)
                                    localUserDetails.userAdvertisementDetails.singleAdvertisementDetails=advDetails
                                    saveBannerDetailsToDB(localUserDetails)
                                }
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
            snackBar(e.message.toString())
        }
    }

    private fun getTimeStamp(startDate: String, startTime: String): String {
        val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val date = formatter.parse("$startDate $startTime") as Date
        return date.time.toString()
    }

    private fun saveBannerDetailsToDB(localUserDetails: UserDetailsModel) {
        dbReference.child(
            getStoredUserDetails().userId
        )
            .setValue(localUserDetails)
            .addOnSuccessListener {
                snackBar("Ads Saved")
                showProgress(false)
            }
            .addOnFailureListener {
                snackBar(it.message.toString())
                showProgress(false)

            }

    }

    private fun generateString(): String? {
        val uuid: String = UUID.randomUUID().toString()
        return uuid.replace("-".toRegex(), "")
    }

    //endregion
}
