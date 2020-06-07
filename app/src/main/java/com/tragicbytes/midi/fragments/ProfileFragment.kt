package com.tragicbytes.midi.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.BuildConfig
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.DashBoardActivity
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.SharedPref.IS_SOCIAL_LOGIN
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_EMAIL
import com.tragicbytes.midi.utils.ImagePicker
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btnChangePassword
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileFragment : BaseFragment() {

    private lateinit var dbReference: DatabaseReference
    private var storageReference: StorageReference? = null
    private var encodedImage: String? = null
    val mAuth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            edtEmail.setText(getEmail())
            edtFirstName.setText(getFirstName())
            edtLastName.setText(getLastName())
            edtFirstName.setSelection(edtFirstName.text.length)
            edtMobileNo.setText(getMobile())
            edtDOB.setText(getDob())
            edtOrg.setText(getOrg())



            ivProfileImage.loadImageFromUrl(
                user!!.photoUrl.toString(),
                aPlaceHolderImage = R.drawable.ic_profile
            )
            if (getSharedPrefInstance().getBooleanValue(IS_SOCIAL_LOGIN)) {
                btnChangePassword.hide()
            } else {
                btnChangePassword.show()
            }
        }
        setUpListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                getSharedPrefInstance().setValue(Constants.SharedPref.USER_PROFILE_URL, resultUri)
                ivProfileImage.setImageURI(resultUri)
                val imageStream = activity!!.contentResolver.openInputStream(resultUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                encodedImage = encodeImage(selectedImage)
                if (encodedImage != null) {
                    updateProfilePhoto(resultUri)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                if (error?.message != null) {
                    snackBar(error.message!!)
                }
            }
        } else {
            if (data != null && data.data != null) ivProfileImage.setImageURI(data.data)
            val path: String? =
                ImagePicker.getImagePathFromResult(activity!!, requestCode, resultCode, data)
                    ?: return
            val uri = FileProvider.getUriForFile(
                activity!!,
                BuildConfig.APPLICATION_ID + ".provider",
                File(path)
            )
            CropImage.activity(uri)
                .setOutputCompressQuality(40)
                .start(activity!!)


        }


    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun setUpListener() {
        btnSaveProFile.onClick {
            if (validate()) {
                showProgress()
                updateProfile()
            }
        }
        btnChangePassword.onClick {
            snackBar(user!!.photoUrl.toString())
           // showChangePasswordDialog()
        }
        btnDeactivate.onClick {

        }
        editProfileImage.onClick {
            activity?.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {
                        /* ImagePicker.pickImage(
                             this@ProfileFragment,
                             context.getString(R.string.lbl_select_image),
                             ImagePicker.mPickImageRequestCode,
                             false
                         )*/
                        CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .setRequestedSize(300, 300)
                            .setOutputCompressQuality(40)
                            .start(context, this@ProfileFragment)
                    } else {
                        activity!!.showPermissionAlert(this)
                    }
                })
        }

    }

    private fun updateProfilePhoto(selectedImage: Uri) {
        showProgress()
        val requestModel = RequestModel()
        requestModel.base64_img = encodedImage
        storageReference = FirebaseStorage.getInstance().reference
        val ref =
            storageReference!!.child("uploads/" + getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_DISPLAY_NAME) + "user_image")
        val uploadTask = ref.putFile(selectedImage)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(downloadUri.toString()))
                    .build()

                FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            hideProgress()
                            snackBar("Profile Pic Uploaded")

                            getSharedPrefInstance().setValue(
                                Constants.SharedPref.USER_PROFILE_URL,
                                FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
                            )
                            (activity as DashBoardActivity).changeProfile()
                            Toast.makeText(
                                context,
                                FirebaseAuth.getInstance().currentUser!!.photoUrl.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            hideProgress()
                        } else {
                            snackBar("Failed Upload")

                        }

                    }
            }
        }



    }

    private fun showChangePasswordDialog() {
        var userEmail = getSharedPrefInstance().getStringValue(USER_EMAIL)

        val changePasswordDialog = Dialog(activity!!)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_reset)
        changePasswordDialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        changePasswordDialog.findViewById<EditText>(R.id.edtResetEmail).setText(getEmail())
        changePasswordDialog.findViewById<EditText>(R.id.edtResetEmail).isEnabled=false

        changePasswordDialog.btnChangePassword.onClick {
            try {
                var userEmail = getSharedPrefInstance().getStringValue(USER_EMAIL)
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {

                            changePasswordDialog.dismiss()
                            snackBar("Forget Password Mail Sent to :$userEmail")

                        }
                        task.isCanceled -> {
                            snackBar("Forget Password Cancelled")
                        }
                    }
                }
            } catch (e: Exception) {
                snackBar(e.message.toString())
            }
        }
        changePasswordDialog.show()
    }

    private fun updateProfile() {
        val user = FirebaseAuth.getInstance().currentUser!!
        dbReference = FirebaseDatabase.getInstance().reference

        if (edtEmail.textToString() != getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_EMAIL)) {
            (activity as AppBaseActivity).updateEmail(user, edtEmail.textToString()) {
                snackBar(it)
                hideProgress()
            }
        }
        if (edtFirstName.textToString() + " " + edtLastName.textToString() != getSharedPrefInstance().getStringValue(
                Constants.SharedPref.USER_DISPLAY_NAME
            )
        ) {
            (activity as AppBaseActivity).updateName(
                user,
                edtFirstName.textToString() + " " + edtLastName.textToString()
            ) {
                snackBar(it)
                hideProgress()
            }
        }
        if (edtMobileNo.textToString() != getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_PHONE)) {
            (activity as AppBaseActivity).updatePhone(
                getSharedPrefInstance().getStringValue(
                    Constants.SharedPref.USER_ID
                ), dbReference, edtMobileNo.textToString()
            ) {
                snackBar(it)
                hideProgress()
            }
        }
        if (edtDOB.textToString() != getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_DOB)) {
            (activity as AppBaseActivity).updateDOB(
                getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_ID),
                dbReference,
                edtDOB.textToString()
            ) {
                snackBar(it)
                hideProgress()
            }
        }
        if (edtOrg.textToString() != getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_ORG)) {
            (activity as AppBaseActivity).updateORG(
                getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_ID),
                dbReference,
                edtOrg.textToString()
            ) {
                snackBar(it)
                hideProgress()
            }
        }
        if (spnGender.selectedItem.toString() != getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_GENDER)) {

            (activity as AppBaseActivity).updateGender(
                getSharedPrefInstance().getStringValue(
                    Constants.SharedPref.USER_ID
                ), dbReference, spnGender.selectedItem.toString()
            ) {
                snackBar(it)
                hideProgress()
            }
        }
        val requestModel = RequestModel()
        requestModel.email = edtEmail.textToString()
        requestModel.first_name = edtFirstName.textToString()
        requestModel.last_name = edtLastName.textToString()
        requestModel.user_org = edtOrg.textToString()
        requestModel.mobile_no = edtMobileNo.textToString()
        (activity as AppBaseActivity).createCustomer(requestModel) {
            snackBar(getString(R.string.lbl_profile_saved))
            hideProgress()
        }
    }

    private fun validate(): Boolean {
        return when {
            edtFirstName.checkIsEmpty() -> {
                edtFirstName.showError(getString(R.string.error_field_required))
                false
            }
            edtLastName.checkIsEmpty() -> {
                edtLastName.showError(getString(R.string.error_field_required))
                false
            }
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            !edtEmail.isValidEmail() -> {
                edtEmail.showError(getString(R.string.error_enter_valid_email))
                false
            }
            edtMobileNo.checkIsEmpty() -> {
                edtMobileNo.showError(getString(R.string.error_field_required))
                false
            }
            edtDOB.checkIsEmpty() -> {
                edtDOB.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }

    }
}