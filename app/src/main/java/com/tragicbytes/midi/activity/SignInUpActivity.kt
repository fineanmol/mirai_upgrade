package com.tragicbytes.midi.activity

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.fragments.SignInFragment
import com.tragicbytes.midi.fragments.SignUpFragment
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.*


class SignInUpActivity : AppBaseActivity() {

    private val mSignInFragment: SignInFragment = SignInFragment()
    private val mSignUpFragment: SignUpFragment = SignUpFragment()
    private var callbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
//    private var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_up)
        FacebookSdk.sdkInitialize(applicationContext)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
//        callbackManager = CallbackManager.Factory.create()
//        LoginManager.getInstance().registerCallback(callbackManager,
//                object : FacebookCallback<LoginResult> {
//                    override fun onSuccess(loginResult: LoginResult) {
//                        authenticate(FacebookAuthProvider.getCredential(loginResult.accessToken.token), loginResult.accessToken.token, "facebook")
//                    }
//
//                    override fun onCancel() {
//                        Log.d("Sign in", "facebook:onCancel")
//                    }
//
//                    override fun onError(error: FacebookException) {
//                        Log.d("Sign in", "facebook:onError", error)
//                    }
//                })
        /**
         * Load Default Fragment
         */
        loadSignInFragment()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        callbackManager?.onActivityResult(requestCode, resultCode, data)
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Constants.RequestCode.SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                authenticate(GoogleAuthProvider.getCredential(account?.idToken, null), account?.idToken!!, "google")
//            } catch (e: ApiException) {
//                Log.w("SIGN IN", "Google sign in failed", e)
//            }
//        }
//    }

//    private fun authenticate(credential: AuthCredential, token: String, type: String) {
//        showProgress(true)
//        mAuth?.signInWithCredential(credential)
//                ?.addOnFailureListener {
//                    snackBar(getString(R.string.lbl_sign_in_failed))
//                }
//                ?.addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        val user = mAuth?.currentUser
//                        //Log.e("toiken","gbgh****\n"+mAuth?.getAccessToken(true)?.result?.token)
//                        doSocialLogin(user!!, token, type)
//                    } else {
//                        snackBar(getString(R.string.lbl_sign_in_failed))
//                        showProgress(false)
//
//                    }
//                }
//    }

    fun loadSignUpFragment() {
        if (mSignUpFragment.isAdded) {
            replaceFragment(mSignUpFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignUpFragment, R.id.fragmentContainer)
        }
    }

    fun loadSignInFragment() {
        if (mSignInFragment.isAdded) {
            replaceFragment(mSignInFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignInFragment, R.id.fragmentContainer)
        }
    }

    override fun onBackPressed() {
        when {
            mSignUpFragment.isVisible -> removeFragment(mSignUpFragment)
            else -> super.onBackPressed()

        }
    }

//    fun doFacebookLogin() {
//        LoginManager.getInstance().logInWithReadPermissions(
//                this,
//                listOf("email", "user_photos", "public_profile")
//        )
//    }

//    fun doGoogleLogin() {
//        val signInIntent = mGoogleSignInClient?.signInIntent
//        startActivityForResult(signInIntent, Constants.RequestCode.SIGN_IN)
//    }

    fun doEmailLogin(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //region LoginMethod
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    var type = "Email"
                    var token = ""
                    if (user != null) {
                        accountDataFetch(user)
                        getSharedPrefInstance().setValue(
                            Constants.SharedPref.USER_PROFILE_URL,
                            user.photoUrl.toString()

                        )
                        getSharedPrefInstance().setValue(
                            Constants.SharedPref.USER_ID,
                            user.uid

                        )
                        getSharedPrefInstance().setValue(
                            Constants.SharedPref.USER_DISPLAY_NAME,
                            user.displayName

                        )
                        signInEmail(user, onResult = {
                            showProgress(false)
                            if (it) launchActivityWithNewTask<DashBoardActivity>()
                        }, onError = {
                            showProgress(false)
                            snackBarError(it)
                        })
                    }

                } else if (task.isCanceled) {
                    showProgress(false)
                    task.exception?.message?.let {
                        snackBar(task.exception?.message!!)
                    }
                } else {
                    showProgress(false)
                    task.exception?.message?.let {

                        snackBar(task.exception?.message!!)
                    }
                }
            }

    }


//    private fun doSocialLogin(user: FirebaseUser, token: String, type: String) {
//        var firstName = ""
//        var lastName = ""
//        if (user.displayName != null && user.displayName?.split(" ")?.size!! >= 2) {
//            firstName = user.displayName?.split(" ")?.get(0)!!
//            lastName = user.displayName?.split(" ")?.get(1)!!
//        } else {
//            firstName = user.displayName!!
//        }
//        getSharedPrefInstance().setValue(Constants.SharedPref.USER_PROFILE,user.photoUrl.toString())
//        socialLogin(user.email!!, token, firstName.trim(), lastName.trim(), type, user.photoUrl.toString(), onResult = {
//            showProgress(false)
//            if (it) launchActivityWithNewTask<DashBoardActivity>()
//        }, onError = {
//            showProgress(false)
//            snackBarError(it)
//        })
//    }

    fun addUser(requestModel: RequestModel) {
        mAuth?.createUserWithEmailAndPassword(
            requestModel.email.toString(),
            requestModel.password.toString()
        )
            ?.addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        val user = mAuth!!.currentUser!!
                        println(user.email.toString())
                        /*Send verification email*/
                        if (!user!!.isEmailVerified) {
                            user.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) snackBar(
                                        "Verification mail sent to " + user.email.toString(),
                                        Snackbar.LENGTH_SHORT
                                    )
                                }
                        }

                        /*Send verification email End*/

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(requestModel.first_name.toString() + " " + requestModel.last_name.toString())
                            .build()
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    println(user.displayName.toString())


                                } else {

                                    println(task.exception.toString())
                                }
                            }

                        createCustomerByEmail(user) {
                            runDelayedOnUiThread(1000) {
                                loadSignInFragment()
                            }
                        }

                    }
                    task.isCanceled -> {
                        showProgress(false)
                        task.exception?.message?.let {
                            snackBar(task.exception?.message!!)
                        }
                    }
                    else -> {
                        showProgress(false)
                        task.exception?.message?.let {
                            snackBar(task.exception?.message!!)
                        }
                    }
                }
            }
    }


    fun accountDataFetch(user: FirebaseUser) {
        showProgress(true)
        val database = FirebaseDatabase.getInstance().reference

        try {
            var localDatabaseRef = database.child(user.uid).child("profileExtras")
            val dataListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    if (dataSnapshot.exists()) {
                        val dbContent =
                            dataSnapshot.getValue(RequestModel.AccountDetails::class.java)
                        if (dbContent != null) {
                            getSharedPrefInstance().setValue(Constants.SharedPref.USER_PHONE, dbContent.Phone)
                            getSharedPrefInstance().setValue(Constants.SharedPref.USER_DOB, dbContent.DOB)
                            getSharedPrefInstance().setValue(Constants.SharedPref.USER_ORG, dbContent.ORG)
                            getSharedPrefInstance().setValue(Constants.SharedPref.USER_GENDER, dbContent.Gender)
                        }

                        showProgress(false)

                    } else {
                        showProgress(false)
                        snackBar("Erorr occurred while fetching details!")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                    // ...
                    showProgress(false)
                    snackBar("Cancelled while fetching details!")

                }
            }
            localDatabaseRef.addValueEventListener(dataListener)
        } catch (e: Exception) {
            e.message?.let { snackBar(it) }
        }
    }
}