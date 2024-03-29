package com.tragicbytes.midi.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.StorageReference
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.fragments.MyBannersFragment
import com.tragicbytes.midi.fragments.ProfileFragment
import com.tragicbytes.midi.fragments.UploadBannerFragment
import com.tragicbytes.midi.fragments.home.HomeFragment
import com.tragicbytes.midi.fragments.home.HomeFragment2
import com.tragicbytes.midi.models.CategoryData
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.layout_sidebar.*
import kotlinx.android.synthetic.main.menu_cart.*
import kotlinx.android.synthetic.main.toolbar.*


class DashBoardActivity : AppBaseActivity() {

    private var selectedDashboard: Int = 0

    //region Variables
    private var count: String = ""
    private lateinit var mHomeFragment: Fragment
    private val mUploadBannerFragment = UploadBannerFragment()
    private val mCartFragment = MyBannersFragment()
    private val mProfileFragment = ProfileFragment()



    var selectedFragment: Fragment? = null
    private lateinit var dbReference: DatabaseReference
    private var storageReference: StorageReference? = null

    //endregion
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        if (checkGooglePlayServices()) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    // 2
                    if (!task.isSuccessful) {
                        Log.w("TAG", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    // 3
                    val token = task.result?.token

                    // 4
                    val msg = "TOKEN$token"
                    Log.d("TAG", msg)
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                })

        } else {
            //You won't be able to send notifications to this device
            Log.w("TAG", "Device doesn't have google play services")
        }
       /* FirebaseMessaging.getInstance().subscribeToTopic(getStoredUserDetails().userId)
            .addOnCompleteListener { task ->
                var msg = "true"
                if (!task.isSuccessful) {
                    msg = "false"
                }
                Log.d("TAG", "subscribed $msg")
            }
        FirebaseMessaging.getInstance().subscribeToTopic("nightowl.developers.miditest")
            .addOnCompleteListener { task ->
                var msg = "true"
                if (!task.isSuccessful) {
                    msg = "false"
                }
                Log.d("TAG", "subscribed $msg")
            }*/
        mHomeFragment = UploadBannerFragment()
        /*selectedDashboard =
            getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_DASHBOARD, 0)
        if (selectedDashboard == 0) {
            mHomeFragment = HomeFragment()
        } *//*else if (selectedDashboard == 1) {
            mHomeFragment = HomeFragment2()
        }*/


        setToolbar(toolbar); setUpDrawerToggle(); loadHomeFragment(); setListener()

        if (isLoggedIn()) {
            loadApis()
            /*setWishCount(); setCartCountFromPref()*/
            llInfo.show()
            tvLogout.text = getString(R.string.lbl_logout)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0)
        } else {
            tvLogout.text = getString(R.string.lbl_sign_in)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login, 0, 0, 0)
        }
        BroadcastReceiverExt(this) {
            onAction(CART_COUNT_CHANGE) {
                setCartCountFromPref()
//                if (mCartFragment.isAdded) mCartFragment.invalidateCartLayout(getCartListFromPref())
            }
            /*onAction(ORDER_COUNT_CHANGE) { setOrderCount() }*/
            onAction(PROFILE_UPDATE) { setUserInfo() }
            /*  onAction(WISHLIST_UPDATE) { setWishCount() }*/
        }
        setUserInfo(); tvVersionCode.text = String.format("%S %S", "V", getAppVersionName())
        // TODO: check in bundle extras for notification data
        /*val bundle = intent.extras
        if (bundle != null) {
            text_view_notification.text = bundle.getString("text")
        }*/
    }

    override fun onResume() {
        super.onResume()
        if (selectedDashboard != getSharedPrefInstance().getIntValue(
                Constants.SharedPref.KEY_DASHBOARD,
                0
            )
        ) {
            recreate()
        }
    }

    private fun loadApis() {
        if (isNetworkAvailable()) {

        }
    }

    //region Clicks
    private fun setListener() {
        civProfile.onClick {
            if (isLoggedIn()) {
                launchActivity<EditProfileActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
            closeDrawer()
        }
        llHome.onClick {
            closeDrawer()
            enable(ivHome)
            loadFragment(HomeFragment())
            title = getString(R.string.lbl_upload_banner)
        }
        adsTextBtn.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            loadHomeFragment()
        }
        llWishList.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivWishList)
            loadFragment(mUploadBannerFragment)
            title = getString(R.string.home)
        }
        adsImagesBtn.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            loadWishListFragment()
            title = getString(R.string.lbl_upload_banner)
        }


        llCart.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivCart)
            tvNotificationCount.hide()
            loadFragment(mCartFragment)
            if (mCartFragment.isAdded) {
//                mCartFragment.invalidateCartLayout(getCartListFromPref())
            }
            title = getString(R.string.my_banners)
        }


        llProfile.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivProfile)
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)
        }
        wallet.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()

            launchActivity<WalletActivity>()
            title = getString(R.string.action_wallet)
        }
        tvAccount.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
            } else {
                launchActivity<AccountActivity>(Constants.RequestCode.ACCOUNT)
            }
            closeDrawer()
        }
        tvSettings.onClick {
            launchActivity<SettingActivity>(requestCode = Constants.RequestCode.SETTINGS)
            closeDrawer()
        }


        paidAdsBtn.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivCart)
            loadFragment(mCartFragment)
            title = getString(R.string.cart)

        }
        profileBtn.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivProfile)
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)

        }
        tvHelp.onClick { launchActivity<HelpActivity>(); closeDrawer() }
        tvContactus.onClick { launchActivity<ContactUsActivity>(); closeDrawer() }
        tvFaq.onClick { launchActivity<FAQActivity>(); closeDrawer() }
        tvAbout.onClick { launchActivity<AboutActivity>(); closeDrawer() }
        ivCloseDrawer.onClick { closeDrawer() }
        tvLogout.onClick {
            if (isLoggedIn()) {
                val dialog = getAlertDialog(
                    getString(R.string.lbl_logout_confirmation),
                    onPositiveClick = { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        clearLoginPref()
                        launchActivityWithNewTask<SignInUpActivity>()

                        //recreate()
                    },
                    onNegativeClick = { dialog, _ ->
                        dialog.dismiss()
                    })
                dialog.show()
                closeDrawer()
            } else {
                launchActivity<SignInUpActivity>()
            }
        }

        tvShareApp.onClick { closeDrawer(); shareMyApp(this@DashBoardActivity, "", "") }

    }
    //endregion

    //region Set Data
    private fun showCartCount() {
        if (isLoggedIn() && !count.checkIsEmpty() && !count.equals("0", false)) {
            tvNotificationCount.hide()
        } else {
            tvNotificationCount.hide()
        }
    }


    private fun setCartCountFromPref() {
        count = getCartCount()
        tvNotificationCount.text = count
        //  showCartCount()
        if (mCartFragment.isVisible) tvNotificationCount.hide()
    }

    private fun setUserInfo() {
        txtDisplayName.text = getUserFullName()
        changeProfile()
    }


    //endregion

    //region Fragment Setups
    private fun loadWishListFragment() {
        enable(ivWishList)
        loadFragment(mUploadBannerFragment)
        title = getString(R.string.lbl_wish_list)
    }

    fun setDrawerCategory(it: ArrayList<CategoryData>) {
        /*  rvCategory.create(
              it.size,
              R.layout.item_navigation_category,
              it,
              getVerticalLayout(false),
              onBindView = { item, position ->
                  tvCategory.text = item.name.getHtmlString()
                  if (item.image != null) {
                      ivCat.loadImageFromUrl(
                          item.image,
                          aPlaceHolderImage = R.drawable.cat_placeholder
                      )
                  }
              },
              itemClick = { item, position ->
                  closeDrawer()
                  launchActivity<SubCategoryActivity> { putExtra(DATA, item) }
              })
          rvCategory.isNestedScrollingEnabled = false*/
    }

    fun loadFragment(aFragment: Fragment) {
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

    internal fun loadHomeFragment() {
        enable(ivWishList)
        //if (!mHomeFragment.isAdded) loadFragment(mHomeFragment) else showFragment(mHomeFragment)
        loadFragment(mHomeFragment)
        title = getString(R.string.home)
        if (mHomeFragment is HomeFragment) {
            (mHomeFragment as HomeFragment).onNetworkRetry = { loadApis() }
        } else if (mHomeFragment is HomeFragment2) {
            (mHomeFragment as HomeFragment2).onNetworkRetry = { loadApis() }
        }
    }
    //endregion

    //region Common
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            !mHomeFragment.isVisible -> loadHomeFragment()
            mHomeFragment.isVisible -> snackBar("Please click BACK again to exit")
            else -> super.onBackPressed()
        }
        this.doubleBackToExitPressedOnce = true

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21) {
            if (resultCode == RESULT_OK && requestCode == Constants.RequestCode.ACCOUNT) {
                //  loadWishListFragment()
            }
        }
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

    private fun setUpDrawerToggle() {
        val toggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                if (WooBoxApp.language == "ar") {
                    main.translationX = -slideOffset * drawerView.width
                } else {
                    main.translationX = slideOffset * drawerView.width
                }
                (drawerLayout).bringChildToFront(drawerView)
                (drawerLayout).requestLayout()
            }
        }
        toggle.setToolbarNavigationClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_drawer,
                theme
            )
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(llLeftDrawer)) runDelayed(50) {
            drawerLayout.closeDrawer(
                llLeftDrawer
            )
        }
    }

    fun changeProfile() {
        if (isLoggedIn()) {
            val user = FirebaseAuth.getInstance().currentUser!!
            civProfile.loadImageFromUrl(
                getStoredUserDetails().userPersonalDetails.userProfilePic,
                aPlaceHolderImage = R.drawable.ic_profile
            )
        }
    }

    override fun onStart() {
        super.onStart()
        //TODO: Register the receiver for notifications
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("MyData"))
    }

    override fun onStop() {
        super.onStop()
        // TODO: Unregister the receiver for notifications
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)

    }

    // TODO: Add a method for receiving notifications
    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
//            text_view_notification.text = intent.extras?.getString("message")
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e("TAG", "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i("TAG", "Google play services updated")
            true
        }
    }





}
