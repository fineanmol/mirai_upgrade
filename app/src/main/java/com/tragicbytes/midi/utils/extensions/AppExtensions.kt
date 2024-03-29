package com.tragicbytes.midi.utils.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.WooBoxApp.Companion.getAppInstance
import com.tragicbytes.midi.WooBoxApp.Companion.noInternetDialog
import com.tragicbytes.midi.activity.*
import com.tragicbytes.midi.models.*
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_BRAND
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_DESC
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_LOGO
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_NAME
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_TAG
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.ORDER_COUNT_CHANGE
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.WISHLIST_UPDATE
import com.tragicbytes.midi.utils.Constants.KeyIntent.DATA
import com.tragicbytes.midi.utils.Constants.PLAY_STORE_URL_PREFIX
import com.tragicbytes.midi.utils.Constants.SharedPref.ADS_BANNER_URL
import com.tragicbytes.midi.utils.Constants.SharedPref.CART_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.CATEGORY_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.DEFAULT_CURRENCY
import com.tragicbytes.midi.utils.Constants.SharedPref.IS_LOGGED_IN
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_ADDRESS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_CART_COUNT
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_DASHBOARD
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_RECENTS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_USER_ADDRESS
import com.tragicbytes.midi.utils.Constants.SharedPref.SLIDER_IMAGES_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.THEME_COLOR
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_DETAILS_OBJECT
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_DISPLAY_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_DOB
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_EMAIL
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_FIRST_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_ID
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_LAST_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_NICE_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_ORG
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_PHONE
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_PROFILE
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_PROFILE_URL
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_ROLE
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_TOKEN
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_USERNAME
import com.tragicbytes.midi.utils.Constants.SharedPref.WISHLIST_DATA
import com.tragicbytes.midi.utils.SharedPrefUtils
import kotlinx.android.synthetic.main.dialog_no_internet.*
import kotlinx.android.synthetic.main.item_banner.view.*
import kotlinx.android.synthetic.main.item_confirm_screen_card.view.*
import kotlinx.android.synthetic.main.item_my_screen_card.view.*
import kotlinx.android.synthetic.main.item_product_new.view.*
import kotlinx.android.synthetic.main.item_product_new.view.ivProduct
import kotlinx.android.synthetic.main.item_product_new.view.tvOriginalPrice
import kotlinx.android.synthetic.main.item_screen.view.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import kotlinx.android.synthetic.main.layout_transaction_card.view.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

fun isLoggedIn(): Boolean = getSharedPrefInstance().getBooleanValue(IS_LOGGED_IN)
fun getUserId(): String = getSharedPrefInstance().getStringValue(USER_ID)
fun getDefaultCurrency(): String = getSharedPrefInstance().getStringValue(DEFAULT_CURRENCY)
fun getThemeColor(): String = getSharedPrefInstance().getStringValue(THEME_COLOR)
fun Context.getUserFullName(): String {
    return when {
        isLoggedIn() -> getStoredUserDetails().userPersonalDetails.firstName + " " + getStoredUserDetails().userPersonalDetails.lastName

        else -> getString(R.string.text_guest_user)
    }
}

fun getUserName(): String = getSharedPrefInstance().getStringValue(USER_USERNAME)
fun getUserfullName(): String = getSharedPrefInstance().getStringValue(USER_DISPLAY_NAME)
fun getFirstName(): String = getStoredUserDetails().userPersonalDetails.firstName
fun getLastName(): String = getStoredUserDetails().userPersonalDetails.lastName
fun getUserProfile(): String = getSharedPrefInstance().getStringValue(USER_PROFILE)
fun getEmail(): String = getStoredUserDetails().userPersonalDetails.email
fun getDob(): String = getStoredUserDetails().userPersonalDetails.dob
fun getOrg(): String = getStoredUserDetails().userPersonalDetails.company
fun getMobile(): String = getStoredUserDetails().userPersonalDetails.phone
fun getProfileUrl(): String = getSharedPrefInstance().getStringValue(USER_PROFILE_URL)
fun getApiToken(): String = getSharedPrefInstance().getStringValue(USER_TOKEN)
fun getCartCount(): String = getSharedPrefInstance().getIntValue(KEY_CART_COUNT, 0).toString()

fun fetchUserData(
    dbReference: DatabaseReference,
    onSuccess: (String) -> Unit,
    onFailed: (String) -> Unit
) {
    dbReference.child("UsersData/" + getStoredUserDetails().userId)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val dbContent =
                        dataSnapshot.getValue(UserDetailsModel::class.java)
                    if (dbContent != null) {
                        Log.d("xxx", "UserDetails Updated!")
                        getSharedPrefInstance().setValue(
                            Constants.SharedPref.USER_DETAILS_OBJECT,
                            Gson().toJson(dbContent)
                        )
                        //     onSuccess("UserDetails Updated")
                    }


                } else {
                    onFailed("Error occurred while fetching details!")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                try {
                    onFailed("Cancelled while fetching details!")
                } catch (e: Exception) {

                }
            }
        })
}

fun getStoredUserDetails(): UserDetailsModel {
    return Gson().fromJson(
        getSharedPrefInstance().getStringValue(USER_DETAILS_OBJECT),
        UserDetailsModel::class.java
    )
}

fun updateStoredUserDetails(userDetailsModel: UserDetailsModel) {
    getSharedPrefInstance().setValue(
        USER_DETAILS_OBJECT,
        Gson().toJson(userDetailsModel)
    )
}



fun updateTransactionDetails(
    newTransactionsDetails: TransactionDetails,
    dbReference: DatabaseReference,
    onSuccess: () -> Unit,
    onFailed: () -> Unit
) {

    var localStoredUserDetails = getStoredUserDetails()
    var transactionsList = localStoredUserDetails.userWalletDetails.transactionsDetails
    transactionsList.add(newTransactionsDetails)
    localStoredUserDetails.userWalletDetails.transactionsDetails = transactionsList
//    updateStoredUserDetails(localStoredUserDetails)
    dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails")
        .setValue(
            transactionsList
        ).addOnSuccessListener {
            dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails/${transactionsList.size - 1}/transactionDate")
                .setValue(
                    ServerValue.TIMESTAMP
                ).addOnSuccessListener {
                    onSuccess()

                }.addOnFailureListener {
                    onFailed()
                }
        }.addOnFailureListener {
            onFailed()
        }


}


fun updateWalletAmount(
    dbReference: DatabaseReference,
    onSuccess: (String) -> Unit,
    onFailed: (String) -> Unit
) {
    try {
        dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/transactionsDetails")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var sum = 0
                            dataSnapshot.children.forEach {
                                var transactionsDetails =
                                    it.getValue(TransactionDetails::class.java)!!
                                if (transactionsDetails.transactionStatus == "1" || transactionsDetails.transactionStatus == "2") {
                                    sum += transactionsDetails.transactionAmount.toInt()
                                }
                            }
                            dbReference.child("UsersData/${getStoredUserDetails().userId}/userWalletDetails/totalAmount")
                                .setValue(sum.toString()).addOnCompleteListener {
                                    val localUserDetails = getStoredUserDetails()
                                    localUserDetails.userWalletDetails.totalAmount =
                                        sum.toString()
/*
                                    updateStoredUserDetails(localUserDetails)
*/
                                    onSuccess(sum.toString())
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        onFailed("")
                    }
                }

            )
    } catch (e: Exception) {
        onFailed("Error: " + e.message)
    }
}

/**
 * Add shared preference related to user session here
 */
fun clearLoginPref() {
    getSharedPrefInstance().removeKey(IS_LOGGED_IN)
    getSharedPrefInstance().removeKey(USER_ID)
    getSharedPrefInstance().removeKey(USER_DISPLAY_NAME)
    getSharedPrefInstance().removeKey(USER_EMAIL)
    getSharedPrefInstance().removeKey(USER_NICE_NAME)
    getSharedPrefInstance().removeKey(USER_TOKEN)
    getSharedPrefInstance().removeKey(USER_FIRST_NAME)
    getSharedPrefInstance().removeKey(USER_LAST_NAME)
    getSharedPrefInstance().removeKey(USER_PROFILE)
    getSharedPrefInstance().removeKey(USER_PROFILE_URL)
    getSharedPrefInstance().removeKey(USER_DOB)
    getSharedPrefInstance().removeKey(USER_ORG)
    getSharedPrefInstance().removeKey(USER_PHONE)
    getSharedPrefInstance().removeKey(USER_FIRST_NAME)
    getSharedPrefInstance().removeKey(USER_LAST_NAME)
    getSharedPrefInstance().removeKey(USER_ROLE)
    getSharedPrefInstance().removeKey(USER_USERNAME)
    getSharedPrefInstance().removeKey(WISHLIST_DATA)
    getSharedPrefInstance().removeKey(CART_DATA)
    getSharedPrefInstance().removeKey(KEY_RECENTS)
    getSharedPrefInstance().removeKey(KEY_DASHBOARD)
    getSharedPrefInstance().removeKey(KEY_ADDRESS)
    getSharedPrefInstance().removeKey(KEY_USER_ADDRESS)
    getSharedPrefInstance().removeKey(USER_DETAILS_OBJECT)
}

fun clearAdsDataPref() {
    getSharedPrefInstance().removeKey(ADV_LOGO)
    getSharedPrefInstance().removeKey(ADV_NAME)
    getSharedPrefInstance().removeKey(ADV_DESC)
    getSharedPrefInstance().removeKey(ADV_TAG)
    getSharedPrefInstance().removeKey(ADV_BRAND)

}

fun getSharedPrefInstance(): SharedPrefUtils {
    return if (WooBoxApp.sharedPrefUtils == null) {
        WooBoxApp.sharedPrefUtils = SharedPrefUtils()
        WooBoxApp.sharedPrefUtils!!
    } else {
        WooBoxApp.sharedPrefUtils!!
    }
}

fun RecyclerView.rvItemAnimation() {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
}


fun Context.openCustomTab(url: String) =
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))

fun ImageView.loadImageFromUrl(
    aImageUrl: String,
    aPlaceHolderImage: Int = R.drawable.placeholder,
    aErrorImage: Int = R.drawable.placeholder
) {
    try {
        if (!aImageUrl.checkIsEmpty()) {
            Glide.with(getAppInstance()).load(aImageUrl).placeholder(aPlaceHolderImage)
                .diskCacheStrategy(DiskCacheStrategy.NONE).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {
    Glide.with(getAppInstance()).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun shareMyApp(context: Context, subject: String, message: String) {
    try {
        val appUrl = PLAY_STORE_URL_PREFIX + context.packageName
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        var leadingText = "\n" + message + "\n\n"
        leadingText += appUrl + "\n\n"
        i.putExtra(Intent.EXTRA_TEXT, leadingText)
        context.startActivity(Intent.createChooser(i, "Share using"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.fontMedium(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_bold))
}

fun Context.fontSemiBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_medium))
}

fun Context.fontBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_semibold))
}

fun Activity.makeTransparentStatusBar() {
    val window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = this.resources.getColor(R.color.item_background)
}

enum class JsonFileCode {
    NO_INTERNET,
    LOADER
}

fun Activity.openLottieDialog(
    jsonFileCode: JsonFileCode = JsonFileCode.NO_INTERNET,
    onLottieClick: () -> Unit
) {
    val jsonFile: String = when (jsonFileCode) {
        JsonFileCode.NO_INTERNET -> "lottie/no_internet.json"
        JsonFileCode.LOADER -> "lottie/loader.json"
    }

    if (noInternetDialog == null) {
        noInternetDialog = Dialog(this, R.style.FullScreenDialog)
        noInternetDialog?.setContentView(R.layout.dialog_no_internet); noInternetDialog?.setCanceledOnTouchOutside(
            false
        ); noInternetDialog?.setCancelable(false)
        noInternetDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        noInternetDialog?.rlLottie?.onClick {
            if (!isNetworkAvailable()) {
                snackBarError(getAppInstance().getString(R.string.error_no_internet))
                return@onClick
            }
            noInternetDialog?.dismiss()
            onLottieClick()
        }
    }
    noInternetDialog?.lottieNoInternet?.setAnimation(jsonFile)
    if (!noInternetDialog!!.isShowing) {
        noInternetDialog?.show()
    }
}

fun Activity.getAlertDialog(
    aMsgText: String,
    aTitleText: String = getString(R.string.lbl_dialog_title),
    aPositiveText: String = getString(R.string.lbl_yes),
    aNegativeText: String = getString(R.string.lbl_no),
    onPositiveClick: (dialog: DialogInterface, Int) -> Unit,
    onNegativeClick: (dialog: DialogInterface, Int) -> Unit
): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(aTitleText)
    builder.setMessage(aMsgText)

    builder.setPositiveButton(aPositiveText) { dialog, which ->
        onPositiveClick(dialog, which)
    }
    builder.setNegativeButton(aNegativeText) { dialog, which ->
        onNegativeClick(dialog, which)
    }
    AlertDialog.Builder(this, R.style.AlertDialog_Background)
    return builder.create()
}

fun Activity.productLayoutParams(): LinearLayout.LayoutParams {
    val width = (getDisplayWidth() / 2.5).toInt()
    val imgHeight = width + (width / 8)
    return LinearLayout.LayoutParams(width, imgHeight)
}

fun startOTPTimer(onTimerTick: (String) -> Unit, onTimerFinished: () -> Unit): CountDownTimer? {
    return object : CountDownTimer(60000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            onTimerTick(
                String.format(
                    "00 : %d",
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
            )
        }

        override fun onFinish() {
            onTimerFinished()
        }
    }
}

fun Activity.showProductDetail(model: ProductDataNew) {
    launchActivity<ProductDetailActivity> {
        putExtra(DATA, model)
    }
    addToRecentProduct(model)
}


fun Activity.selectScreen(
    view: View,
    model: ScreenDataModel,
    onSelect: (Int) -> Unit,
    onUnSelect: (Int) -> Unit
) {
    if (model.screenStatus == "1") {
        if (view.screenSelectButton.isChecked) {
            onUnSelect(model.screenPrice.toInt())
        } else {
            onSelect(model.screenPrice.toInt())
        }
        view.screenSelectButton.isChecked = !view.screenSelectButton.isChecked
    } else {
        snackBar("This screen is currently out of service.")
    }
}

fun Activity.showTransactionDetail(model: TransactionDetails) {
    launchActivity<TransactionDetailsActivity> {
        putExtra(DATA, model)
    }
}

fun Activity.showMyBannerDetails(model: SingleAdvertisementDetails) {
    launchActivity<MyBannerDetailsActivity> {
        putExtra(DATA, model)
    }
}

fun Activity.showMyBannerScreenDetails(model: ScreenDataModel) {
    launchActivity<MyBannerScreenDetailsActivity> {
        putExtra(DATA, model)
    }
}


fun Activity.showBannerDetail(model: AdDetailsModel.AdsCompleteDetails) {
    launchActivity<ProductDetailActivity> {
        putExtra(DATA, model)
    }
}

fun Activity.sendCartCountChangeBroadcast() {
    sendBroadcast(CART_COUNT_CHANGE)
}

fun Activity.sendProfileUpdateBroadcast() {
    sendBroadcast(PROFILE_UPDATE)
}

fun Activity.sendWishListBroadcast() {
    sendBroadcast(WISHLIST_UPDATE)
}

fun Activity.sendOrderCountChangeBroadcast() {
    sendBroadcast(ORDER_COUNT_CHANGE)
}

fun Activity.sendBroadcast(action: String) {
    val intent = Intent()
    intent.action = action
    sendBroadcast(intent)
}

fun setExpandableListViewHeight(listView: ExpandableListView, group: Int) {
    val listAdapter = listView.expandableListAdapter as ExpandableListAdapter
    var totalHeight = 0
    var item = 0
    val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY)
    for (i in 0 until listAdapter.groupCount) {
        val groupItem = listAdapter.getGroupView(i, false, null, listView)
        groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)

        totalHeight += groupItem.measuredHeight
        item = groupItem.measuredHeight

        if (((listView.isGroupExpanded(i)) && (i != group)) || ((!listView.isGroupExpanded(i)) && (i == group))) {
            for (j in 0 until listAdapter.getChildrenCount(i)) {
                val listItem = listAdapter.getChildView(
                    i, j, false, null,
                    listView
                )
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }
    }

    val params = listView.layoutParams
    var height =
        totalHeight + (listView.dividerHeight * (listAdapter.groupCount - 1)) + (item / 2)/*+((totalHeight/(listAdapter.groupCount-1)))/2)*/
    if (height < 10)
        height = 200
    params.height = height
    listView.layoutParams = params
    listView.requestLayout()
}

fun AppBaseActivity.getOrders(page: Int = 1, onApiSuccess: (ArrayList<MyOrderData>) -> Unit) {
    /*callApi(getRestApis().listAllOrders(getUserId().toInt(), page), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_ORDERS, Gson().toJson(it))
        sendOrderCountChangeBroadcast()
        onApiSuccess(it)
    }, onApiError = {
        snackBarError(it)
    }, onNetworkError = {
        noInternetSnackBar()
    })*/
}

fun AppBaseActivity.createCustomer(requestModel: RequestModel, onApiSuccess: (LoginData) -> Unit) {
    showProgress(true)
    /*callApi(getRestApis().createCustomer(id = getUserId(), request = requestModel), onApiSuccess = {
        showProgress(false)
        getSharedPrefInstance().setValue(USER_DISPLAY_NAME, it.first_name + " " + it.last_name)
        getSharedPrefInstance().setValue(USER_EMAIL, it.email)
        getSharedPrefInstance().setValue(USER_FIRST_NAME, it.first_name)
        getSharedPrefInstance().setValue(USER_LAST_NAME, it.last_name)
        getSharedPrefInstance().setValue(USER_DOB, it.user_dob)
        getSharedPrefInstance().setValue(USER_ORG, it.user_org)


        onApiSuccess(it)
        sendProfileUpdateBroadcast()
    }, onApiError = {
        showProgress(false)
      //  snackBarError(it)
    }, onNetworkError = {
        openLottieDialog {
           // createCustomer(requestModel, onApiSuccess)
        }
    })*/
}

fun AppBaseActivity.createCustomerByEmail(userDetails: UserDetailsModel, onApiSuccess: () -> Unit) {
    showProgress(true)
    showProgress(false)

    getSharedPrefInstance().setValue(USER_DETAILS_OBJECT, Gson().toJson(userDetails))

//    getSharedPrefInstance().setValue(USER_EMAIL, userDetails.userPersonalDetails?.email)
//    getSharedPrefInstance().setValue(USER_DISPLAY_NAME, userDetails.userPersonalDetails?.firstName+" "+userDetails.userPersonalDetails?.lastName)
//    getSharedPrefInstance().setValue(USER_FIRST_NAME, userDetails.userPersonalDetails?.firstName)
//    getSharedPrefInstance().setValue(USER_LAST_NAME, userDetails.userPersonalDetails?.lastName)
    onApiSuccess()
    sendProfileUpdateBroadcast()
//    }
//    catch(e:Exception){
//        println(e.message.toString()+ " errorrrrrr")
//    }

}

//fun AppBaseActivity.signIn(email: String, password: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
//    val requestModel = RequestModel()
//    requestModel.username = email
//    requestModel.password = password
//    showProgress(true)
//    callApi(getRestApis(false).login(request = requestModel), onApiSuccess = {
//        saveLoginResponse(it,false, password, onResult, onError)
//    }, onApiError = {
//        showProgress(false)
//        onResult(false)
//        onError(it)
//    }, onNetworkError = {
//        showProgress(false)
//        openLottieDialog() {
//            signIn(email, password, onResult, onError)
//        }
//    })
//}

fun AppBaseActivity.signInEmail(
    user: FirebaseUser,
    onResult: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    showProgress(true)
//    callApi(getRestApis(false).login(request = requestModel), onApiSuccess = {
//        saveLoginResponse(it,false, password, onResult, onError)
//    }, onApiError = {
//        showProgress(false)
//        onResult(false)
//        onError(it)
//    }, onNetworkError = {
//        showProgress(false)
//        openLottieDialog() {
//            signInEmail(user, onResult, onError)
//        }
//    })
    saveLoginResponse(user, false, "", onResult, onError)
}


//fun AppBaseActivity.socialLogin(email:String,accessToken: String, firstName: String, lastName: String, loginType: String, photoURL: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
//    val requestModel = RequestModel()
//    requestModel.email = email
//    requestModel.accessToken = accessToken
//    requestModel.firstName = firstName
//    requestModel.lastName = lastName
//    requestModel.loginType = loginType
//    requestModel.photoURL = photoURL
//    showProgress(true)
//    callApi(getRestApis(false).socialLogin(request = requestModel), onApiSuccess = {
//        saveLoginResponse(it,true, accessToken, onResult, onError)
//    }, onApiError = {
//        showProgress(false)
//        onResult(false)
//        onError(it)
//    }, onNetworkError = {
//        showProgress(false)
//        openLottieDialog() {
//            socialLogin(email,accessToken, firstName, lastName, loginType, photoURL, onResult, onError)
//        }
//    })
//}

fun AppBaseActivity.saveLoginResponse(
    it: FirebaseUser,
    isSocialLogin: Boolean,
    password: String,
    onResult: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
//    if (it.uid != null) {
//        getSharedPrefInstance().setValue(USER_ID, it.uid)
//    }
//    getSharedPrefInstance().setValue(USER_DISPLAY_NAME, it.displayName)
//    getSharedPrefInstance().setValue(USER_EMAIL, it.email)
//    getSharedPrefInstance().setValue(USER_NICE_NAME, it.displayName)
//    getSharedPrefInstance().setValue(USER_TOKEN, it.providerId)
//    if (it.displayName?.isNotEmpty()!!){
//        getSharedPrefInstance().setValue(USER_PROFILE, it.displayName)
//    }
//    getSharedPrefInstance().setValue(IS_SOCIAL_LOGIN,isSocialLogin)
//    getSharedPrefInstance().setValue(Constants.SharedPref.USER_PASSWORD, password)
//
//    getSharedPrefInstance().setValue(Constants.SharedPref.SHOW_SWIPE, true)
//    var firstName = ""
//    var lastName = ""
//    if (it.displayName != null && it.displayName?.split(" ")?.size!! >= 2) {
//        firstName = it.displayName?.split(" ")?.get(0)!!
//        lastName = it.displayName?.split(" ")?.get(1)!!
//    } else {
//        var userName = it.displayName!!
//    }
//    getSharedPrefInstance().setValue(USER_FIRST_NAME, firstName)
//    getSharedPrefInstance().setValue(USER_LAST_NAME, lastName)
//    //getSharedPrefInstance().setValue(USER_ROLE, response.role)
//    getSharedPrefInstance().setValue(USER_USERNAME, it.email)
    getSharedPrefInstance().setValue(IS_LOGGED_IN, true)
    onResult(true)
    /*callApi(getRestApis().retrieveCustomer(), onApiSuccess = { response ->
    showProgress(false)
    getSharedPrefInstance().setValue(Constants.SharedPref.SHOW_SWIPE, true)
    getSharedPrefInstance().setValue(USER_FIRST_NAME, response.first_name)
    getSharedPrefInstance().setValue(USER_LAST_NAME, response.last_name)
    getSharedPrefInstance().setValue(USER_ROLE, response.role)
    getSharedPrefInstance().setValue(USER_USERNAME, response.username)
    getSharedPrefInstance().setValue(IS_LOGGED_IN, true)
    onResult(true)
}, onApiError = {
    showProgress(false)
    onResult(false)
    onError(it)
}, onNetworkError = {
    showProgress(false)
    noInternetSnackBar()
})*/
}

fun AppBaseActivity.processPayment(
    requestModel: RequestModel,
    isContainRedirectUrl: Boolean = true
) {
    showProgress(true)
    /*callApi(getRestApis().processPayment(requestModel), onApiSuccess = {
        showProgress(false)
        if (it.data != null) {
            callApi(getRestApis(false).clearCartItems(), onApiSuccess = {
                fetchAndStoreCartData()
                if (!isContainRedirectUrl) launchActivityWithNewTask<DashBoardActivity>()
            })
            if (isContainRedirectUrl) openCustomTab(it.data.redirect)
        }
    }, onApiError = {
        snackBarError(it)
        showProgress(false)
    }, onNetworkError = {
        showProgress(false)
        openLottieDialog() {
            processPayment(requestModel)
        }
    })*/
}

fun recentProduct(): ArrayList<ProductDataNew> {
    val string = getSharedPrefInstance().getStringValue(KEY_RECENTS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<ProductDataNew>>() {}.type)
}

fun addToRecentProduct(product: ProductDataNew) {
    val list = recentProduct()
    list.clear()
    val pos = getPositionIfExist(list, product)
    if (pos != -1) {
        list.removeAt(pos)
    }
    list.add(product)
    getSharedPrefInstance().setValue(KEY_RECENTS, Gson().toJson(list))
}

fun getPositionIfExist(list: ArrayList<ProductDataNew>, product: ProductDataNew): Int {
    list.forEachIndexed { i: Int, productModel: ProductDataNew ->
        if (product.pro_id == productModel.pro_id) {
            return i
        }
    }
    return -1
}

fun getAddressList(): ArrayList<Address> {
    val string = getSharedPrefInstance().getStringValue(KEY_USER_ADDRESS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<Address>>() {}.type)
}

fun getCartPositionIfExist(list: ArrayList<CartResponse>, product: CartResponse): Int {
    list.forEachIndexed { i: Int, productModel: CartResponse ->
        if (product.pro_id == productModel.pro_id) {
            return i
        }
    }
    return -1
}

fun AppBaseActivity.updateItem(product: CartResponse) {
    val list = getCartListFromPref()
    val pos = getCartPositionIfExist(list, product)
    if (pos != -1) {
        list.set(pos, product)
    }
    getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(list))
}


fun AppBaseActivity.getCartTotal(): Int? {
    val list = getCartListFromPref()
    var count = 0
    for (i in 0 until list.size) {
        if (list[i].sale_price.isNotEmpty()) {
            count += list[i].sale_price.toInt() * list[i].quantity.toInt()
        } else {
            if (list[i].price.isNotEmpty()) {
                count += list[i].price.toFloat().toInt() * list[i].quantity.toInt()
            }
        }
    }
    tvOffer.text = getString(R.string.text_offer_not_available)
    tvShippingCharge.text = getString(R.string.lbl_free)
    tvTotalAmount.text = count.toString().currencyFormat()
    return count
}

fun getCartTotalAmount(): Int? {
    val list = getCartListFromPref()
    var count = 0
    for (i in 0 until list.size) {
        if (list[i].sale_price.isNotEmpty()) {
            count += list[i].sale_price.toInt() * list[i].quantity.toInt()
        } else {
            if (list[i].price.isNotEmpty()) {
                count += list[i].price.toFloat().toInt() * list[i].quantity.toInt()
            }
        }
    }
    return count
}

fun isExistInCart(product: ProductDataNew, selectedColor: Boolean = false): Boolean {
    if (getSharedPrefInstance().getStringValue(CART_DATA) == "") {
        return false
    }
    val cartList = Gson().fromJson<ArrayList<CartResponse>>(
        getSharedPrefInstance().getStringValue(CART_DATA),
        object : TypeToken<ArrayList<CartResponse>>() {}.type
    )
    if (cartList != null && cartList.size > 0) {
        cartList.forEachIndexed { i: Int, model: CartResponse ->
            if (product.pro_id == model.pro_id.toInt()) {
                if (selectedColor) {
                    /*product.size = model.size
                    product.color = model.color*/
                }
                return true
            }
        }
    }
    return false
}

fun getCartListFromPref(): ArrayList<CartResponse> {
    if (getSharedPrefInstance().getStringValue(CART_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<CartResponse>>(
        getSharedPrefInstance().getStringValue(CART_DATA),
        object : TypeToken<ArrayList<CartResponse>>() {}.type
    )
}

fun getWishListFromPref(): ArrayList<WishListData> {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<WishListData>>(
        getSharedPrefInstance().getStringValue(
            WISHLIST_DATA
        ), object : TypeToken<ArrayList<WishListData>>() {}.type
    )
}

fun isExistInWishList(product: ProductDataNew): Boolean {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return false
    }
    val wishList = Gson().fromJson<ArrayList<WishListData>>(
        getSharedPrefInstance().getStringValue(WISHLIST_DATA),
        object : TypeToken<ArrayList<WishListData>>() {}.type
    )
    if (wishList != null && wishList.size > 0) {
        wishList.forEachIndexed { i: Int, model: WishListData ->
            if (product.pro_id == model.pro_id) {
                return true
            }
        }
    }
    return false
}

fun Activity.fetchAndStoreCartData() {
    /*callApi(getRestApis(false).getCart(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_CART_COUNT, it.size); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(it)); sendCartCountChangeBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_CART_COUNT, 0); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(ArrayList<CartResponse>()))
            sendCartCountChangeBroadcast()
        } else {
           // snackBarError(it)
        }
    })*/
}

fun Activity.fetchAndStoreWishListData() {
    /*callApi(getRestApis(false).getWishList(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, it.size); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(it)); sendWishListBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, 0); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(ArrayList<WishListData>()))
            sendWishListBroadcast()
        } else {
           // snackBarError(it)
        }
    })*/
}

fun Activity.addToWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    /* callApi(getRestApis(false).addWishList(request = requestModel), onApiSuccess = {
         fetchAndStoreWishListData(); onSuccess(true)
     }, onApiError = {
         snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
     }, onNetworkError = {
         noInternetSnackBar(); onSuccess(false)
     })*/
}

fun Activity.removeFromWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    /*callApi(getRestApis(false).removeWishList(request = requestModel), onApiSuccess = {
        fetchAndStoreWishListData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })*/
}

fun getSlideImagesFromPref(): ArrayList<SliderImagesResponse> {
    if (getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson(
        getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA),
        object : TypeToken<ArrayList<SliderImagesResponse>>() {}.type
    )
}

fun getCategoryDataFromPref(): ArrayList<CategoryData> {
    if (getSharedPrefInstance().getStringValue(CATEGORY_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson(
        getSharedPrefInstance().getStringValue(CATEGORY_DATA),
        object : TypeToken<ArrayList<CategoryData>>() {}.type
    )
}

fun setScreenItem(
    view: View,
    item: ScreenDataModel,
    context: LocationBasedScreensActivity
) {
    /** Close the expand menu at first time*/
    view.screenSelectButton.isChecked = false
    view.expandableLayout.visibility = View.GONE
    view.expandBtn.setOnClickListener {
        if (view.expandableLayout.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(view.cardView, AutoTransition())
            view.expandableLayout.visibility = View.VISIBLE
            val result = rotate(180F, context)!!
            view.show.setCompoundDrawablesWithIntrinsicBounds(result, null, null, null)
            view.show.text = "Show Less"
        } else {
            TransitionManager.beginDelayedTransition(view.cardView, AutoTransition())
            view.expandableLayout.visibility = View.GONE
            val result = rotate(0F, context)!!
            view.show.setCompoundDrawablesWithIntrinsicBounds(result, null, null, null)
            view.show.text = "Show More"

        }
        view.pieChart.animateY(1500, Easing.EasingOption.EaseInBounce)
    }

    setScreenData(view, item, context)

    setChartData(view, item, context)
    setAgeDistributionChartData(view, item.screenAgeGroupPref, context)


}

fun rotate(
    degree: Float,
    context: LocationBasedScreensActivity
): Drawable? {
    val iconBitmap: Bitmap =
        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_arrow_down, null)!!
            .toBitmap()!!
    val matrix = Matrix()
    matrix.postRotate(degree)
    val targetBitmap = Bitmap.createBitmap(
        iconBitmap,
        0,
        0,
        iconBitmap.width,
        iconBitmap.height,
        matrix,
        true
    )
    return BitmapDrawable(context.resources, targetBitmap)
}


fun setScreenData(
    view: View,
    item: ScreenDataModel,
    context: LocationBasedScreensActivity
) {
    view.screenTitle.text = item.screenId
    view.screenStatus.text = "• ${item.screenStatus}"
    if (item.screenStatus == "0") {
        view.screenStatus.setTextColor(ContextCompat.getColor(context, R.color.track_red))
        view.screenStatus.text = "• Stopped"
    } else {
        view.screenStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
        view.screenStatus.text = "• Running"
    }
    view.screenActiveTime.text= item.screenActiveTime
    view.locationTitle.text = item.screenLocation
    view.addressTitle.text = item.screenCity + "," + item.screenPincode
    view.screen_price.text = "${item.screenPrice}".currencyFormat("INR")
}

fun setChartData(
    view: View,
    item: ScreenDataModel,
    locationBasedScreensActivity: LocationBasedScreensActivity
) {
    var maleValue = item.screenGenderRatio.toFloat()
    view.pieChart.setUsePercentValues(true)
    val xvalues = ArrayList<PieEntry>()
    xvalues.add(PieEntry(maleValue, "Male"))
    xvalues.add(PieEntry(100 - maleValue, "Female"))
    val dataSet = PieDataSet(xvalues, "")
    val data = PieData(dataSet)

    // In Percentage
    data.setValueFormatter(PercentFormatter())

    view.pieChart.data = data
    dataSet.setColors(
        intArrayOf(R.color.pie1, R.color.pie2),
        locationBasedScreensActivity
    )
    dataSet.sliceSpace = 5f
    view.pieChart.description.text = "Gender Ratio"
    view.pieChart.description.textSize = 12f
    view.pieChart.isDrawHoleEnabled = false
    data.setValueTextSize(13f)
    chartDetails(view.pieChart, Typeface.SANS_SERIF)
}

fun chartDetails(mChart: PieChart, tf: Typeface) {
    mChart.description.isEnabled = true
    mChart.centerText = ""
    mChart.setCenterTextSize(10F)
    mChart.setCenterTextTypeface(tf)
    val l = mChart.legend
    mChart.legend.isWordWrapEnabled = true
    mChart.legend.isEnabled = true
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    l.formSize = 20F
    l.formToTextSpace = 5f
    l.form = Legend.LegendForm.DEFAULT
    l.textSize = 12f
    l.orientation = Legend.LegendOrientation.HORIZONTAL
    l.isWordWrapEnabled = true
    l.setDrawInside(true)
    mChart.setTouchEnabled(true)
    mChart.setDrawEntryLabels(true)
    mChart.legend.isWordWrapEnabled = true
    mChart.setExtraOffsets(0f, 0f, 20f, 0f)
    mChart.setUsePercentValues(true)
    // mChart.rotationAngle = 0f
    mChart.setUsePercentValues(true)
    mChart.setDrawCenterText(true)
    mChart.description.isEnabled = true
    mChart.isRotationEnabled = true
}


fun setAgeDistributionChartData(
    view: View,
    screenAgeGroupPref: AgeGroupDetail,
    context: LocationBasedScreensActivity
) {


    var skillRatingChart : HorizontalBarChart = view.ageWiseChartGraph

    skillRatingChart.setDrawBarShadow(false)
    val description = Description()
    description.text = ""
    skillRatingChart.description = description
    skillRatingChart.legend.setEnabled(false)
    skillRatingChart.setPinchZoom(false)
    skillRatingChart.setDrawValueAboveBar(false)

    //Display the axis on the left (contains the labels 1*, 2* and so on)
    val xAxis = skillRatingChart.getXAxis()
    xAxis.setDrawGridLines(false)
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
    xAxis.setEnabled(true)
    xAxis.setDrawAxisLine(false)


    val yLeft = skillRatingChart.axisLeft

//Set the minimum and maximum bar lengths as per the values that they represent
    yLeft.axisMaximum = 100f
    yLeft.axisMinimum = 0f
    yLeft.isEnabled = false

    //Set label count to 5 as we are displaying 5 star rating
    xAxis.labelCount = 4

//Now add the labels to be added on the vertical axis
    val values = arrayOf("50+", "45-50", "18-34", "Below 18")
    xAxis.valueFormatter = XAxisValueFormatter(values)

    val yRight = skillRatingChart.axisRight
    yRight.setDrawAxisLine(true)
    yRight.setDrawGridLines(false)
    yRight.isEnabled = false

    //Set bar entries and add necessary formatting
    setGraphData(screenAgeGroupPref, skillRatingChart, view)

    //Add animation to the graph
    skillRatingChart.animateY(2000, Easing.EasingOption.EaseInBounce)
}


fun setGraphData(
    screenAgeGroupPref: AgeGroupDetail,
    skillRatingChart: HorizontalBarChart,
    view: View
) {
    //Add a list of bar entries
    val entries = ArrayList<BarEntry>()
    entries.add(BarEntry(0f, screenAgeGroupPref.generationZ.toFloat()))
    entries.add(BarEntry(1f, screenAgeGroupPref.generationY.toFloat()))
    entries.add(BarEntry(2f, screenAgeGroupPref.generationX.toFloat()))
    entries.add(BarEntry(3f, screenAgeGroupPref.babyBoomers.toFloat()))

    //Note : These entries can be replaced by real-time data, say, from an API

    val barDataSet = BarDataSet(entries, "Bar Data Set")

    //Set the colors for bars with first color for 1*, second for 2* and so on
    barDataSet.setColors(
        ContextCompat.getColor(skillRatingChart.context, R.color.green),
        ContextCompat.getColor(skillRatingChart.context, R.color.pie1),
        ContextCompat.getColor(skillRatingChart.context, R.color.pie2),
        ContextCompat.getColor(skillRatingChart.context, R.color.track_yellow)
    )

    //Set bar shadows
    view.ageWiseChartGraph.setDrawBarShadow(true)
//        barDataSet.barShadowColor = Color(40, 150, 150, 150)
    val data = BarData(barDataSet)

    //Set the bar width
    //Note : To increase the spacing between the bars set the value of barWidth to < 1f
    data.barWidth = 0.9f

    //Finally set the data and refresh the graph
    view.ageWiseChartGraph.data = data
    view.ageWiseChartGraph.invalidate()

}


fun setOrderedScreenData(
    view: View,
    item: ScreenDataModel,
    singleAdvertisementDetails: SingleAdvertisementDetails,
    context: MyBannerDetailsActivity
) {

    view.bScreenName.text = item.screenLocation
    view.bScreenId.text = item.screenId
    view.bScreenPrice.text = item.screenPrice.currencyFormat("INR")
    view.bSubmittedDate.text = getShortDate(singleAdvertisementDetails.advSubmittedOn)
    view.tvApprovedTitle.text = "Status Pending"
    view.ivCircleSubmitted.setCircleColor(ContextCompat.getColor(context, R.color.yellow))


    /** 1==Screen Ads Approved*/
    if (item.screenApprovedStatus == "1") {
        view.bApprovedDate.visibility =View.VISIBLE
        view.tvDelivered.visibility= View.VISIBLE
        view.bApprovedDate.text = getShortDate(item.screenAdvApprovedOn)
        view.ivCircleApproved.setCircleColor(ContextCompat.getColor(context, R.color.green))
        view.tvApprovedTitle.setTextColor(ContextCompat.getColor(context, R.color.green))
        view.tvApprovedTitle.text = "Adv. Approved"
        if (item.screenAdminComment.isNullOrEmpty()) {
            view.tvDelivered.text = "Your Advertisement has been Published"
        } else {
            view.tvDelivered.text = item.screenAdminComment
        }
    }

    /** 0==Screen Ads Rejected*/
    if (item.screenApprovedStatus == "2") {
        view.bApprovedDate.visibility =View.VISIBLE
        view.tvDelivered.visibility= View.VISIBLE
        view.ivCircleSupport.visibility=View.VISIBLE
        view.ivCircleSupport.onClick {
            if(view.tvRefundMsg.visibility==View.VISIBLE){
                view.tvRefundMsg.visibility=View.GONE
            }
            else
            view.tvRefundMsg.visibility=View.VISIBLE
        }
        view.bApprovedDate.text = getShortDate(item.screenAdvApprovedOn)
        view.tvDelivered.text = item.screenAdminComment
        view.tvApprovedTitle.text = "Adv. Rejected"
        view.ivCircleApproved.setCircleColor(ContextCompat.getColor(context, R.color.red))
        view.tvApprovedTitle.setTextColor(ContextCompat.getColor(context, R.color.track_red))

        if (item.screenAdminComment.isNullOrEmpty() || item.screenAdminComment=="Your Advertisement is under Review") {
            view.tvDelivered.text = "Your Advertisement has been Rejected"
        } else {
            view.tvDelivered.text = item.screenAdminComment
        }
    }





}


fun setProductItem(view: View, item: ProductDataNew) {
    view.tvProductName.text = item.name
   /* if (item.sale_price!!.isNotEmpty()) {
        view.tvDiscountPrice.text = item.sale_price.currencyFormat()
    } else {
        view.tvDiscountPrice.text = item.price?.currencyFormat()
    }
   // view.ratingBar.rating = item.average_rating!!.toFloat()
  //  view.tvOriginalPrice.text = item.regular_price?.currencyFormat()
  //  view.tvOriginalPrice.applyStrike()*/
    if (item.full != null) view.ivProduct.loadImageFromUrl(item.full)
}

fun setWalletItem(
    view: View,
    item: TransactionDetails,
    context: WalletTransactionsActivity
) {
    view.tPaymentId.text = "Payment Id\n" + item.transactionId

    if (item.transactionStatus == "1") {
        if (item.transactionAmount.isNotEmpty()) {
            view.tAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
            view.tAmount.text = "+ ₹" + item.transactionAmount
            view.tIcon.setBackgroundResource(R.drawable.ic_checkbox_circle_fill)
            view.tTransactionText.text = "Money Added"
            view.tPaymentId.isGone()


        } else {
            view.tAmount.text = item.transactionAmount.currencyFormat()

        }
    } else if (item.transactionStatus == "0") {
        if (item.transactionAmount.isNotEmpty()) {
            view.tAmount.setTextColor(ContextCompat.getColor(context, R.color.textColorSecondary))
            view.tAmount.text = "  ₹" + item.transactionAmount
            view.tIcon.setBackgroundResource(R.drawable.ic_round_cancel_24px)
            view.tTransactionText.text = "Add Money Failed"

        } else {
            view.tAmount.text = item.transactionAmount.currencyFormat()


        }
    } else if (item.transactionStatus == "2") {
        if (item.transactionAmount.isNotEmpty()) {
            view.tAmount.setTextColor(ContextCompat.getColor(context, R.color.black))
            view.tAmount.text = "- ₹" + item.transactionAmount.split("-").last().toString()
            view.tIcon.setBackgroundResource(R.drawable.ad_icon) /**ic_star_black*/
            view.tTransactionText.text = "Paid to Advertisement"

        } else {
            view.tAmount.text = "+ ₹" + item.transactionAmount

        }
    }
    else if (item.transactionStatus == "3") {
        if (item.transactionAmount.isNotEmpty()) {
            view.tAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
            view.tAmount.text = "+ ₹" + item.transactionAmount.split("-").last().toString()
            view.tIcon.setBackgroundResource(R.drawable.ic_checkbox_circle_fill)
            view.tTransactionText.text = "Amount Reverted"
            view.tRefund.visibility=View.VISIBLE

        } else {
            view.tAmount.text = "+ ₹" + item.transactionAmount

        }
    }
    view.tDate.text = getShortDate(item.transactionDate)

}

fun getShortDate(ts: Long?): String {
    if (ts == null) return ""
    //Get instance of calendar
    val calendar = Calendar.getInstance(Locale.getDefault())
    //get current date from ts
    calendar.timeInMillis = ts
    //return formatted date
    return android.text.format.DateFormat.format("E, dd MMM yyyy", calendar).toString()
}

fun getShortTime(ts: Long?): String {
    if (ts == null) return ""
    //Get instance of calendar
    val calendar = Calendar.getInstance(Locale.getDefault())
    //get current date from ts
    calendar.timeInMillis = ts
    //return formatted date
    return android.text.format.DateFormat.format("hh:mm a", calendar).toString()
}

// Used to convert 24hr format to 12hr format with AM/PM values
fun updateTime(hours: Int, mins: Int): String {
    var hours = hours
    var timeSet = ""
    when {
        hours > 12 -> {
            hours -= 12
            timeSet = "PM"
        }
        hours == 0 -> {
            hours += 12
            timeSet = "AM"
        }
        hours == 12 -> timeSet = "PM"
        else -> timeSet = "AM"
    }
    var minutes = ""
    minutes = if (mins < 10) "0$mins" else mins.toString()

    val aTime = StringBuilder().append(hours).append(':')
        .append(minutes).append(" ").append(timeSet).toString()
    return aTime

}

fun setSelectedScreenItem(
    view: View,
    item: ScreenDataModel,
    ongoingAdv: SingleAdvertisementDetails,
    context: ConfirmationActivity
) {
    var dateDifference= ((ongoingAdv.endOn.toLong()-ongoingAdv.startFrom.toLong())/(1000*60*60*24)+1).toInt()
    var totalPrice= item.screenPrice.toInt() * dateDifference
    view.tScreenLocation.text ="Loc : "+ item.screenLocation
    view.tTime.text = item.screenActiveTime
    view.tScreenCity.text = item.screenCity +", "+ item.screenPincode
    view.tScreenName.text = "Id: "+item.screenId
    view.tTimeDistribution.text = item.screenActiveTime
    view.tGenderRatio.text = item.screenGenderRatio
    view.tAgeDistributtion.text =   "\nBelow 18 ="+item.screenAgeGroupPref.generationZ +"%"+"\n18-34 ="+item.screenAgeGroupPref.generationY+"%"+"\n35-50 ="+item.screenAgeGroupPref.generationX+"%"+ "\n50+ ="+item.screenAgeGroupPref.babyBoomers+"%"
    view.tScreenPrice.text = item.screenPrice.currencyFormat("INR")+ " * "+ dateDifference.toString()+ " = " + totalPrice.toString().currencyFormat("INR")

}


fun setBannerData(
    view: View,
    item: SingleAdvertisementDetails,
    position: Int
) {
    if (item.advBannerUrl != null) {
        fetchImageAsync(item.advBannerUrl,
            onComplete = {
                view.ivBannerPrev.setImageBitmap(it)
            })
        view.tvBannerId.text = "Banner ${position + 1}"
        view.tvBannerEndDate.text = "Expires On ${getShortDate(item.endOn.toLong())}"

        if(item.screens.size.toInt()>0 || !item.screens.isNullOrEmpty() )
        {
        item.screens.forEach { screenDataModel: ScreenDataModel ->
            if(!screenDataModel.screenApprovedStatus.isNullOrEmpty()){
                view.txt_review.text = "Adv Reviewed"
                view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape_black)
            }
            else{
                view.txt_review.text = "Pending Review"
                view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape_black)
            }
        }
        }

       /* if (item.advOverallStatus == "0" || item.advOverallStatus == "") {
            view.txt_review.text = "Pending Review"
            view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape_black)
        } else if (item.advOverallStatus == "1") {
            view.txt_review.text = "Adv Reviewed"
            view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape_black)
        } else if (item.advOverallStatus == "2") {
            view.txt_review.text = "Partially Live"
            view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape_green)
        } else if (item.advOverallStatus == "3") {
            view.txt_review.text = "Rejected"
            view.txt_review_layout.setBackgroundResource(R.drawable.ic_review_shape)
        }*/
    }
}

fun Activity.fetchAndStoreAddressData() {
    /*callApi(getRestApis(false).getAddress(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_USER_ADDRESS, Gson().toJson(it))
        sendBroadcast(ADDRESS_UPDATE)
        Log.e("response",Gson().toJson(it))
    }, onApiError = {

    }, onNetworkError = {
        noInternetSnackBar()
    })*/
}

fun Activity.addAddress(address: Address, onSuccess: (Boolean) -> Unit) {
    /*callApi(getRestApis(false).addUpdateAddress(address), onApiSuccess = {
        fetchAndStoreAddressData()
        onSuccess(true)
    }, onApiError = {
        snackBarError(it); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })*/
}

fun Activity.removeAddress(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    /*callApi(getRestApis(false).deleteAddress(request = requestModel), onApiSuccess = {
        fetchAndStoreAddressData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreAddressData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })*/
}

fun Activity.changePassword(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    /*callApi(getRestApis().changePassword(getUserId().toInt(), requestModel), onApiSuccess = {
        snackBar(getString(R.string.msg_successpwd));onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })*/
}

fun Activity.saveProfileImage(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    /*callApi(getRestApis().saveProfileImage( requestModel), onApiSuccess = {
        Log.e("res",it.profile_image)
        getSharedPrefInstance().setValue(USER_PROFILE, it.profile_image)
        onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })*/
}

@SuppressLint("MissingPermission")
fun Activity.saveLogoImageToStorage(
    mContext: Context,
    storageReference: StorageReference,
    personalizedBannerBitmap: Bitmap,
    onSuccess: (String) -> Unit,
    onUploading: (Float) -> Unit,
    onFailed: (String) -> Unit,
    onUploadStart: () -> Unit
) {
    var file = File.createTempFile("image", null, mContext.cacheDir)
    personalizedBannerBitmap.saveAsync(
        file.path
    ) {
        val ref =
            storageReference.child("uploads/" + getStoredUserDetails().userId + System.currentTimeMillis())
        val uploadTask = ref.putFile(Uri.fromFile(file))
        var showDialog = true
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (task.result.bytesTransferred > 0 && showDialog) {
                val dialog = getAlertDialog(
                    "While your Banner is processing, Please continue with next details",
                    "Information",
                    onPositiveClick = { dialog, i ->
                        onUploadStart()
                    },
                    onNegativeClick = { dialog, i ->
                        dialog.dismiss()
                    })
                dialog.show()
                showDialog = false
            }
            onUploading((task.result.bytesTransferred / task.result.totalByteCount) * 100F)
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                getSharedPrefInstance().setValue(ADS_BANNER_URL, downloadUri)
                onSuccess(downloadUri.toString())
            } else {
                onFailed(task.exception!!.localizedMessage)
                snackBar(task.exception!!.localizedMessage)
            }
        }.addOnFailureListener {
            snackBar(it.localizedMessage)
        }
    }
}


fun AppBaseActivity.saveProfileImageToStorage(
    mContext: Context,
    dbReference: DatabaseReference,
    storageReference: StorageReference,
    personalizedBannerBitmap: Bitmap,
    onSuccess: (Boolean) -> Unit
) {
    var file = File.createTempFile("image", null, mContext.cacheDir)
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    personalizedBannerBitmap.saveAsync(
        file.path
    ) {
        val ref =
            storageReference.child(
                "uploads/" + getSharedPrefInstance().getStringValue(
                    USER_DISPLAY_NAME
                ) + "_Profile_Pic"
            )
        val uploadTask = ref.putFile(Uri.fromFile(file))
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                dbReference.child(getSharedPrefInstance().getStringValue(USER_ID))
                    .child("profileExtras/User_Profile").setValue(downloadUri.toString())
                    .addOnCompleteListener {
                        if (task.isSuccessful) {
                            onSuccess(true)
                        }
                    }
            } else {
                snackBar(task.exception!!.localizedMessage);onSuccess(false)
            }
        }.addOnFailureListener {
            snackBar(it.localizedMessage);onSuccess(false)
        }
    }
}

fun Activity.addAdvertisement(
    advDetails: SingleAdvertisementDetails,
    onSuccess: (SingleAdvertisementDetails) -> Unit
) {


    try {
        getSharedPrefInstance().setValue(ADV_DESC, advDetails.advDescription)
        getSharedPrefInstance().setValue(ADV_BRAND, advDetails.advBrandName)
        getSharedPrefInstance().setValue(ADV_NAME, advDetails.advName)
        getSharedPrefInstance().setValue(ADV_TAG, advDetails.advTagline)
        onSuccess(advDetails)
    } catch (e: java.lang.Exception) {
        snackBar(e.localizedMessage)
    }
//    dbReference.child(getSharedPrefInstance().getStringValue(USER_ID)).child("adDetails").setValue(adDetails).addOnCompleteListener {
//        if(it.isSuccessful){
//            onSuccess(adDetails)
//            snackBar("Ads Data Saved",Snackbar.LENGTH_SHORT)
//        }
//        else{
//            snackBarError(it.exception!!.localizedMessage);
//        }
//    }
}

fun AppBaseActivity.updateEmail(
    user: FirebaseUser,
    newUserEmail: String,
    dbReference: DatabaseReference,
    onApiSuccess: (String) -> Unit
) {

    user.updateEmail(newUserEmail).addOnCompleteListener { xit ->
        if (xit.isSuccessful) {
            dbReference.child("UsersData/${user.uid}/userPersonalDetails/email")
                .setValue(user.email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showProgress(false)
                        var localUserdata = getStoredUserDetails()
                        localUserdata.userPersonalDetails.email = user.email.toString()
                        updateStoredUserDetails(localUserdata)
                        onApiSuccess("Email Updated!!!")
                        sendProfileUpdateBroadcast()
                    } else {
                        showProgress(false)
                        snackBarError(it.exception!!.localizedMessage)
                    }
                }
        } else {
            showProgress(false)
            snackBarError(xit.exception!!.localizedMessage)
        }
    }

}

fun AppBaseActivity.updateName(
    user: FirebaseUser,
    displayName: String,
    dbReference: DatabaseReference,
    onApiSuccess: (String) -> Unit
) {
    val profileUpdates = UserProfileChangeRequest.Builder()
        .setDisplayName(displayName)
        .build()
    user.updateProfile(profileUpdates).addOnCompleteListener { xit ->
        if (xit.isSuccessful) {
            dbReference.child("UsersData/${user.uid}/userPersonalDetails/firstName")
                .setValue(user.displayName?.split(" ")?.first()!!).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dbReference.child("UsersData/${user.uid}/userPersonalDetails/lastName")
                            .setValue(user.displayName?.split(" ")?.last()!!)
                        showProgress(false)
                        var localUserData = getStoredUserDetails()
                        localUserData.userPersonalDetails.firstName =
                            user.displayName?.split(" ")?.first()!!
                        localUserData.userPersonalDetails.lastName =
                            user.displayName?.split(" ")?.last()!!
                        updateStoredUserDetails(localUserData)
                        onApiSuccess("Name Updated!!!")
                        sendProfileUpdateBroadcast()
                    } else {
                        showProgress(false)
                        snackBarError(it.exception!!.localizedMessage)
                    }
                }
        } else {
            showProgress(false)
            snackBarError(xit.exception!!.localizedMessage)
        }
    }
}

fun AppBaseActivity.updatePhone(
    userId: String,
    dbReference: DatabaseReference,
    phone: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child("UsersData/${userId}/userPersonalDetails/phone").setValue(phone)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                var localUserData = getStoredUserDetails()
                localUserData.userPersonalDetails.phone = phone
                updateStoredUserDetails(localUserData)
                onApiSuccess("Phone Updated")
            } else {
                showProgress(false)
                snackBarError(it.exception!!.localizedMessage)
            }
        }

}

fun AppBaseActivity.updateProfileUrl(
    userId: String,
    dbReference: DatabaseReference,
    profileUrl: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/ProfileUrl").setValue(profileUrl)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                getSharedPrefInstance().setValue(USER_PROFILE_URL, profileUrl)
                onApiSuccess("Profile Pic Updated")
            } else {
                showProgress(false)
                snackBarError(it.exception!!.localizedMessage)
            }

        }

}

fun AppBaseActivity.updateDOB(
    userId: String,
    dbReference: DatabaseReference,
    dob: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child("UsersData/${userId}/userPersonalDetails/dob").setValue(dob)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                var localUserData = getStoredUserDetails()
                localUserData.userPersonalDetails.dob = dob
                updateStoredUserDetails(localUserData)
                onApiSuccess("DOB Updated")
            } else {
                showProgress(false)
                snackBarError(it.exception!!.localizedMessage)
            }
        }
}

fun AppBaseActivity.updateORG(
    userId: String,
    dbReference: DatabaseReference,
    org: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child("UsersData/${userId}/userPersonalDetails/company").setValue(org)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                var localUserData = getStoredUserDetails()
                localUserData.userPersonalDetails.company = org
                updateStoredUserDetails(localUserData)
                onApiSuccess("Organization Name Updated")
            } else {
                showProgress(false)
                snackBarError(it.exception!!.localizedMessage)
            }
        }
}

fun AppBaseActivity.updateGender(
    userId: String,
    dbReference: DatabaseReference,
    gender: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child("UsersData/${userId}/userPersonalDetails/gender").setValue(gender)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                var localUserData = getStoredUserDetails()
                localUserData.userPersonalDetails.gender = gender
                updateStoredUserDetails(localUserData)
                onApiSuccess("Gender Updated")
            } else {
                showProgress(false)
                snackBarError(it.exception!!.localizedMessage)
            }
        }
}

