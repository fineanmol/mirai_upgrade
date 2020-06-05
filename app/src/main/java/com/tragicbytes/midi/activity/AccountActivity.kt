package com.tragicbytes.midi.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.toolbar.*


class AccountActivity : _root_ide_package_.nightowl.tragicbytes.midi.AppBaseActivity() {
    val user = FirebaseAuth.getInstance().currentUser!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setToolbar(toolbar)
        title = getString(R.string.title_account)
        user.reload()

        txtDisplayName.text = getUserFullName()
        ivProfileImage.loadImageFromUrl(user.photoUrl.toString(), aPlaceHolderImage = R.drawable.ic_profile)
        if(!user.isEmailVerified) btnVerify.text="Verify Email" else verifyColumn.hide()


        btnSignOut.onClick {
            val dialog = getAlertDialog(
                getString(R.string.lbl_logout_confirmation),
                onPositiveClick = { dialog, i ->
                    clearLoginPref()
                    FirebaseAuth.getInstance().signOut()
                    launchActivity<_root_ide_package_.nightowl.tragicbytes.midi.activity.SignInUpActivity>()
                },
                onNegativeClick = { dialog, i ->
                    dialog.dismiss()
                })
            dialog.show()
        }

        ivProfileImage.onClick { launchActivity<_root_ide_package_.nightowl.tragicbytes.midi.activity.EditProfileActivity>() }
        tvHelpCenter.onClick { launchActivity<_root_ide_package_.nightowl.tragicbytes.midi.activity.HelpActivity>() }
        btnVerify.onClick {
            /*Send verification email*/
            if (!user.isEmailVerified) {

                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) snackBar(
                            "Verification mail sent to " + user.email.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                    }
                user.reload()
            }
            /*Send verification email End*/
            else if(user.phoneNumber==null){
                /* launchActivity<OTPActivity>()*/
            }



        }
        tvAddressManager.onClick {
            if (getAddressList().size == 0) {
                launchActivity<_root_ide_package_.nightowl.tragicbytes.midi.activity.AddAddressActivity>()
            } else {
                launchActivity<_root_ide_package_.nightowl.tragicbytes.midi.activity.AddressManagerActivity>()
            }
        }
        /*   tvWishlist.onClick {
               setResult(Activity.RESULT_OK)
               finish()
           }*/
        showBannerAds()
    }
}
