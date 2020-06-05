package com.tragicbytes.midi

import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.tragicbytes.midi.network.RestApis
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.SharedPref.LANGUAGE
import com.tragicbytes.midi.utils.LocaleManager
import com.tragicbytes.midi.utils.SharedPrefUtils
import com.tragicbytes.midi.utils.extensions.getSharedPrefInstance
import com.onesignal.OneSignal
import okhttp3.OkHttpClient
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class WooBoxApp : MultiDexApplication() {

    private var userUploadImageEncoded: String? = null



    fun getUserUploadImageEncoded(): String? {
        return userUploadImageEncoded
    }

    fun setUserUploadImageEncoded(userUploadImageEncoded: String?) {
        this.userUploadImageEncoded = userUploadImageEncoded
    }
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        getSharedPrefInstance().apply {
            appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            language = getStringValue(LANGUAGE,"en")
        }

        // Set Custom Font
        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_regular)).setFontAttrId(
                        R.attr.fontPath
                ).build()
        )

        MobileAds.initialize(this) {}

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
        OneSignal.clearOneSignalNotifications()
    }


    fun enableNotification(isEnabled: Boolean) {
        OneSignal.setSubscription(isEnabled)
    }

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var localeManager: LocaleManager
        private lateinit var appInstance: WooBoxApp
        var restApis: RestApis? = null
        var okHttpClient: OkHttpClient? = null
        var sharedPrefUtils: SharedPrefUtils? = null
        var noInternetDialog: Dialog? = null
        lateinit var language: String
        var appTheme: Int = 0

        fun getAppInstance(): WooBoxApp {
            return appInstance
        }

        fun changeAppTheme(isDark: Boolean) {
            getSharedPrefInstance().apply {
                when {
                    isDark -> setValue(Constants.SharedPref.THEME, Constants.THEME.DARK)
                    else -> setValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
                }
                appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            }

        }

        fun changeLanguage(aLanguage: String) {
            getSharedPrefInstance().setValue(LANGUAGE, aLanguage)
            language = aLanguage
        }


    }
}
