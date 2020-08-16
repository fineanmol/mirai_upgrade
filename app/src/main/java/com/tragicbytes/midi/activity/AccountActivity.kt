package com.tragicbytes.midi.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.fragments.ProfileFragment
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.txtDisplayName
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.menu_cart.*
import kotlinx.android.synthetic.main.toolbar.*


class AccountActivity : AppBaseActivity() {
    val user = FirebaseAuth.getInstance().currentUser!!

    private val mProfileFragment = ProfileFragment()
    var selectedFragment: Fragment? = null



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
                        launchActivity<SignInUpActivity>()
                    },
                    onNegativeClick = { dialog, i ->
                        dialog.dismiss()
                    })
            dialog.show()
        }

        ivProfileImage.onClick { launchActivity<EditProfileActivity>() }
        tvHelpCenter.onClick { launchActivity<HelpActivity>() }
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
                launchActivity<AddAddressActivity>()
            } else {
                launchActivity<AddressManagerActivity>()
            }
        }
        profileButton.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }

            enable(ivProfile)
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)

        }
        /*   tvWishlist.onClick {
               setResult(Activity.RESULT_OK)
               finish()
           }*/
        showBannerAds()
    }

    private fun loadFragment(aFragment: Fragment) {
        if (selectedFragment != null) {
            if (selectedFragment == aFragment) return
            hideFragment(selectedFragment!!)
        }
        if (aFragment.isAdded) {
            showFragment(aFragment)
        } else {

            addFragment(aFragment, R.id.container)
        }
        selectedFragment = aFragment
    }
    private fun enable(aImageView: ImageView?) {
        disableAll()
        // showCartCount()
        aImageView?.background = getDrawable(R.drawable.bg_circle_primary_light)
        aImageView?.applyColorFilter(color(R.color.colorPrimary))
    }
    private fun disableAll() {
        disable(ivHome)
        disable(ivWishList)
        disable(ivCart)
        disable(ivProfile)
    }

    private fun disable(aImageView: ImageView?) {
        aImageView?.background = null
        aImageView?.applyColorFilter(color(R.color.textColorSecondary))
    }

}
