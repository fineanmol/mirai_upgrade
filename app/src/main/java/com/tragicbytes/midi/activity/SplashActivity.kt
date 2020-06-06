package com.tragicbytes.midi.activity

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.tragicbytes.FirebaseConfig
import com.tragicbytes.midi.BuildConfig
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.getSharedPrefInstance
import com.tragicbytes.midi.utils.extensions.launchActivity
import com.tragicbytes.midi.utils.extensions.runDelayed
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : FirebaseConfig() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mFirebaseRemoteConfig = getRemoteConfigValues()
        setRemoteConfigValues()

        try {
            val info = packageManager.getPackageInfo(packageName, GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
                }
            }
        } catch (e: NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
        /*runDelayed(1000) {
            if (getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)) {
                launchActivity<DashBoardActivity>()
            } else {
                launchActivity<WalkThroughActivity>()
            }
            finish()
        }*/
    }

    //region Firebase Config Method 2
    private fun setRemoteConfigValues() {
        //region Fetching Values
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong("versionCode")
        val AlertTitle = mFirebaseRemoteConfig.getString("Alert_Title")
        val AlertMessage = mFirebaseRemoteConfig.getString("Alert_Message")
        val Alert_Ok_btn = mFirebaseRemoteConfig.getString("Alert_Ok_Btn")
        val Alert_No_btn = mFirebaseRemoteConfig.getString("Alert_No_Btn")

        //endregion

        //region App Update Dialog Box
        if (remoteCodeVersion > 0) {
            val versionCode = BuildConfig.VERSION_CODE
            if (remoteCodeVersion > versionCode) {
                //region DialogBox
                val dialogBuilder = AlertDialog.Builder(this)


                // set message of alert dialog
                dialogBuilder.setMessage(AlertMessage)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(Alert_Ok_btn, DialogInterface.OnClickListener { _, _ ->
                        val uri =
                            Uri.parse("market://details?id=" + this@SplashActivity.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@SplashActivity.packageName)
                                )
                            )
                        }
                    })
                    // negative button text and action
                    .setNegativeButton(Alert_No_btn, // do something when the button is clicked
                        DialogInterface.OnClickListener { _, _ ->
                            finishAffinity()
                        })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(AlertTitle)
                // show alert dialog
                alert.show()
                //endregion
            }
            //  main_layout!!.setBackgroundColor(Color.parseColor(remoteValueText))
        }
        //endregion

    }
    //endregion

    //region Firebase Config Method 3
    override fun onStart() {

        super.onStart()
        mFirebaseRemoteConfig = getRemoteConfigValues()
        //region Startup Notification Firebase Config
        val remoteCodeVersion = mFirebaseRemoteConfig.getLong("versionCode")
        val versionCode = BuildConfig.VERSION_CODE

        if (remoteCodeVersion > versionCode) {
            getRemoteConfigValues()
        } else {
            runDelayed(1000) {
                if (getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)) {
                    launchActivity<DashBoardActivity>()
                } else {
                    launchActivity<WalkThroughActivity>()
                }
                finish()
            }
        }

        //endregion
    }
    //endregion
}