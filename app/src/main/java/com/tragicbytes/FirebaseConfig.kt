package com.tragicbytes

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.BuildConfig
import com.tragicbytes.midi.R


open class FirebaseConfig : AppCompatActivity() {

    var firebaseLogged = false
    open lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var firebaseUser:FirebaseUser? = null

    fun firebaseRemoteConfig() {
        //region Firebase Config Setup
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.firebasedefaults)
    }

    //region Firebase Config Method 1
    internal fun getRemoteConfigValues(): FirebaseRemoteConfig {

        if (!firebaseLogged) {
            firebaseRemoteConfig()
        }
        var cacheExpiration: Long = 7200//2 hours

        // Allow fetch on every call for now - remove/comment on production builds
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //   Toast.makeText(this, "Fetch Succeeded", Toast.LENGTH_SHORT).show()
                    mFirebaseRemoteConfig.activateFetched()
                    firebaseLogged = true
                } else {
                    //   Toast.makeText(this, "Fetch Failed", Toast.LENGTH_SHORT).show()
                }

            }
        return mFirebaseRemoteConfig
    }

    fun getFirebaseUser(): FirebaseUser? {
        if(firebaseUser==null){
            firebaseUser= FirebaseAuth.getInstance().currentUser
        }
        return firebaseUser
    }

    fun getUserProfile(){

    }
}
