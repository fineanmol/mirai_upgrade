package com.tragicbytes.midi.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.core.content.FileProvider
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.BuildConfig
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.AdDetailsModel
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_BRAND
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_DESC
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_LOGO
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_NAME
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_TAG
import com.tragicbytes.midi.utils.ImagePicker
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_advertisement_form.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream
import java.io.File


class AdvertisementFormActivity : AppBaseActivity() {
    private var encodedImage: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement_form)
        setToolbar(toolbar)

        title = getString(R.string.lbl_edit_form)
        /*val bmp = drawTextToBitmap(this, R.drawable.banner1, "Hello Android")!!
        ivAdsImage.setImageBitmap(bmp)*/
        showProgress(true)
        loadStoredData()

        setUpListener()

    }

    private fun loadStoredData() {
        if (getSharedPrefInstance().getStringValue(ADV_LOGO).isNotEmpty()) {
            val imageAsBytes =
                Base64.decode(getSharedPrefInstance().getStringValue(ADV_LOGO), Base64.DEFAULT)
            ivAdsImage.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageAsBytes,
                    0,
                    imageAsBytes.size
                )
            )
            encodedImage=getSharedPrefInstance().getStringValue(ADV_LOGO)
        }
        else{
            var defaultImageBitmap=BitmapFactory.decodeResource(applicationContext.resources,
                R.drawable.ic_profile)
            ivAdsImage.setImageBitmap(defaultImageBitmap)
            encodedImage=encodeImage(defaultImageBitmap)

        }
        if (getSharedPrefInstance().getStringValue(ADV_NAME).isNotEmpty()) {
            editAdsName.setText(getSharedPrefInstance().getStringValue(ADV_NAME))
            edtAdDescription.setText(getSharedPrefInstance().getStringValue(ADV_DESC))
            edtAdTagline.setText(getSharedPrefInstance().getStringValue(ADV_TAG))
            edtAdBrandName.setText(getSharedPrefInstance().getStringValue(ADV_BRAND))
        }
        showProgress(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                ivAdsImage.setImageURI(resultUri)
                val imageStream =
                    this@AdvertisementFormActivity!!.contentResolver.openInputStream(resultUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                encodedImage = encodeImage(selectedImage)
                if (encodedImage != null) {
                    getSharedPrefInstance().setValue(ADV_LOGO, encodedImage)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                if (error?.message != null) {
                    snackBar(error.message!!)
                }
            }
        } else {
            if (data != null && data.data != null) ivProfileImage.setImageURI(data.data)
            val path: String? = ImagePicker.getImagePathFromResult(
                this@AdvertisementFormActivity!!,
                requestCode,
                resultCode,
                data
            ) ?: return
            val uri = FileProvider.getUriForFile(
                this@AdvertisementFormActivity!!,
                BuildConfig.APPLICATION_ID + ".provider",
                File(path)
            )
            CropImage.activity(uri)
                .setOutputCompressQuality(40)
                .start(this@AdvertisementFormActivity!!)
        }


    }


    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun setUpListener() {
        btnSaveAdsData.onClick {
            showProgress(true)
            if (validate()) {
                var adDetails = AdDetailsModel.AdDetails(
                    editAdsName.textToString(),
                    edtAdDescription.textToString(),
                    edtAdTagline.textToString(),
                    edtAdBrandName.textToString(),
                    encodedImage.toString()
                )

                addAdvertisement(adDetails,
                    onSuccess = {
                        showProgress(false)
                        if (it != null) {
                            val intent = Intent()
                            intent.putExtra("AdvFormData", it)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                            /*launchActivity<ProductDetailActivity> { putExtra("AdvFormData", it) }
                                                setResult(Activity.RESULT_OK)
                        finish()*/
                        }
                    }
                )
            }
            showProgress(false)
        }



        editLogoImage.onClick {
            this@AdvertisementFormActivity?.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {

                        CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .setRequestedSize(300, 300)
                            .setOutputCompressQuality(40)
                            .start(this@AdvertisementFormActivity)
                        // .start(context,this@AdvertisementFormActivity)

                    } else {
                        this@AdvertisementFormActivity!!.showPermissionAlert(this)
                    }
                })
        }

    }
    /*private fun updateProfilePhoto() {
        showProgress(true)
        val requestModel = RequestModel()
        requestModel.base64_img = encodedImage
        this@AdvertisementFormActivity!!.saveLogoImageToStorage(this,dbReference,storageReference!!,requestModel, onSuccess = {
            showProgress(false)
            encodedImage=null
            *//* (activity as DashBoardActivity).changeProfile()*//*
        })

    }*/

    private fun validate(): Boolean {
        return when {
            editAdsName.checkIsEmpty() -> {
                editAdsName.showError(getString(R.string.error_field_required))
                false
            }
            edtAdDescription.checkIsEmpty() -> {
                edtAdDescription.showError(getString(R.string.error_field_required))
                false
            }
            edtAdTagline.checkIsEmpty() -> {
                edtAdTagline.showError(getString(R.string.error_field_required))
                false
            }

            edtAdBrandName.checkIsEmpty() -> {
                edtAdBrandName.showError(getString(R.string.error_field_required))
                false
            }
            /*editLogoImage.() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }*/

            else -> true
        }

    }


}