package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.fragments.DashboardListFragment
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_launguage_selection.*
import kotlinx.android.synthetic.main.spinner_language.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.system.exitProcess

class SettingActivity :AppBaseActivity() {
    private lateinit var lan: String
    private var codes = arrayOf(
        "en",
        "hi",
        "fr",
        "es",
        "de",
        "in",
        "af",
        "pt",
        "tr",
        "ar",
        "vi"
    )
    private var mCountryImg = intArrayOf(
        R.drawable.us,
        R.drawable.india,
        R.drawable.france,
        R.drawable.spain,
        R.drawable.germany,
        R.drawable.indonesia,
        R.drawable.south_africa,
        R.drawable.portugal,
        R.drawable.turkey,
        R.drawable.ar,
        R.drawable.vietnam

    )

    private var mIsSelectedByUser = false
    private var mDashboardListFragment: DashboardListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.title_setting)
        setToolbar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        lan =WooBoxApp.language
        val languages = resources.getStringArray(R.array.language)
        /** to Enable dark mode uncomment below line */
      //  switchNightMode.isChecked = WooBoxApp.appTheme == Constants.THEME.DARK


        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_launguage_selection)
        val languageAdapter =
           RecyclerViewAdapter<String>(
                R.layout.spinner_language,
                onBind = { view: View, s: String, i: Int ->

                    view.ivLogo.setImageResource(mCountryImg[i])
                    view.tvName.text = languages[i]
                })
        languageAdapter.onItemClick = { i: Int, view: View, s: String ->
            /** for multi language change below mCountryImg[0],languages[0] to mCountryImg[i],languages[i] */

            ivLanguage.loadImageFromDrawable(mCountryImg[0])
            tvLanguage.text = languages[0]
            dialog.dismiss()
            /** for multi language change below codes[0] to codes[i] */
            setNewLocale(codes[0])
            if(i>0){
                snackBar("Multi-Language Coming Soon",Snackbar.LENGTH_SHORT)
            }
        }
        dialog.listLanguage.apply {
            setVerticalLayout()
            adapter = languageAdapter
        }
        languageAdapter.addItems(languages.toCollection(ArrayList()))
        llLanguage.onClick {
            dialog.show()
        }
        /*llDashboard.onClick {
            if (mDashboardListFragment == null) {
                mDashboardListFragment = DashboardListFragment.newInstance()
            }
            mDashboardListFragment?.show(supportFragmentManager, DashboardListFragment.tag)
        }*/
        codes.forEachIndexed { i: Int, s: String ->
            if (lan == s) {
                ivLanguage.loadImageFromDrawable(mCountryImg[i])
                tvLanguage.text = languages[i]
            }
        }
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) snackBar("Notifications Enabled",Snackbar.LENGTH_SHORT) else snackBar("Notifications Disabled",Snackbar.LENGTH_SHORT)

            /** to Enable Notifications uncomment below line */
            WooBoxApp.getAppInstance().enableNotification(isChecked)
        }
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            snackBar("Dark Mode Coming Soon",Snackbar.LENGTH_SHORT)

            /** to Enable dark mode uncomment below two lines */
           /* WooBoxApp.changeAppTheme(isChecked)
            switchToDarkMode(isChecked)*/
        }
        Handler().postDelayed({ mIsSelectedByUser = true }, 1000)

    }

    override fun onBackPressed() {

        if (lan != WooBoxApp.language) {
            launchActivityWithNewTask<DashBoardActivity>()
            exitProcess(0)
        } else {
            super.onBackPressed()
        }
    }

    private fun setNewLocale(language: String) {
        WooBoxApp.changeLanguage(language)
        Log.e("lan", language)
        if (lan != language) {
            recreate()
            setResult(Activity.RESULT_OK)
        }
    }
}
