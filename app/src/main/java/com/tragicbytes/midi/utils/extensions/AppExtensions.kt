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
import android.graphics.Typeface
import android.net.Uri
import android.os.CountDownTimer
import android.util.Base64
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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.WooBoxApp.Companion.getAppInstance
import com.tragicbytes.midi.WooBoxApp.Companion.noInternetDialog
import com.tragicbytes.midi.activity.DashBoardActivity
import com.tragicbytes.midi.activity.ProductDetailActivity
import com.tragicbytes.midi.models.*
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_BRAND
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_DESC
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_LOGO
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_NAME
import com.tragicbytes.midi.utils.Constants.AdvDetails.ADV_TAG
import com.tragicbytes.midi.utils.Constants.AppBroadcasts.ADDRESS_UPDATE
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
import com.tragicbytes.midi.utils.Constants.SharedPref.IS_SOCIAL_LOGIN
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_ADDRESS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_CART_COUNT
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_DASHBOARD
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_ORDERS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_RECENTS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_USER_ADDRESS
import com.tragicbytes.midi.utils.Constants.SharedPref.KEY_WISHLIST_COUNT
import com.tragicbytes.midi.utils.Constants.SharedPref.SLIDER_IMAGES_DATA
import com.tragicbytes.midi.utils.Constants.SharedPref.THEME_COLOR
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_DISPLAY_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_DOB
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_EMAIL
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_FIRST_NAME
import com.tragicbytes.midi.utils.Constants.SharedPref.USER_GENDER
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
import kotlinx.android.synthetic.main.item_product_new.view.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import java.io.*
import java.util.concurrent.TimeUnit

fun isLoggedIn(): Boolean = getSharedPrefInstance().getBooleanValue(IS_LOGGED_IN)
fun getUserId(): String = getSharedPrefInstance().getStringValue(USER_ID)
fun getDefaultCurrency(): String = getSharedPrefInstance().getStringValue(DEFAULT_CURRENCY)
fun getThemeColor(): String = getSharedPrefInstance().getStringValue(THEME_COLOR)
fun Context.getUserFullName(): String {
    return when {
        isLoggedIn() -> (getSharedPrefInstance().getStringValue(USER_FIRST_NAME) + " " + getSharedPrefInstance().getStringValue(
                USER_LAST_NAME
        )).toCamelCase()
        else -> getString(R.string.text_guest_user)
    }
}

fun getUserName(): String = getSharedPrefInstance().getStringValue(USER_USERNAME)
fun getUserfullName(): String = getSharedPrefInstance().getStringValue(USER_DISPLAY_NAME)
fun getFirstName(): String = getSharedPrefInstance().getStringValue(USER_FIRST_NAME)
fun getLastName(): String = getSharedPrefInstance().getStringValue(USER_LAST_NAME)
fun getUserProfile(): String = getSharedPrefInstance().getStringValue(USER_PROFILE)
fun getEmail(): String = getSharedPrefInstance().getStringValue(USER_EMAIL)
fun getDob(): String = getSharedPrefInstance().getStringValue(USER_DOB)
fun getOrg(): String = getSharedPrefInstance().getStringValue(USER_ORG)
fun getMobile(): String = getSharedPrefInstance().getStringValue(USER_PHONE)
fun getProfileUrl(): String = getSharedPrefInstance().getStringValue(USER_PROFILE_URL)
fun getApiToken(): String = getSharedPrefInstance().getStringValue(USER_TOKEN)
fun getCartCount(): String = getSharedPrefInstance().getIntValue(KEY_CART_COUNT, 0).toString()


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

fun Context.openCustomTab(url: String) = CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))

fun ImageView.loadImageFromUrl(aImageUrl: String, aPlaceHolderImage: Int = R.drawable.placeholder, aErrorImage: Int = R.drawable.placeholder) {
    try {
        if (!aImageUrl.checkIsEmpty()) {
            Glide.with(getAppInstance()).load(aImageUrl).placeholder(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {
    Glide.with(getAppInstance()).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(this)
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

fun Activity.openLottieDialog(jsonFileCode: JsonFileCode = JsonFileCode.NO_INTERNET, onLottieClick: () -> Unit) {
    val jsonFile: String = when (jsonFileCode) {
        JsonFileCode.NO_INTERNET -> "lottie/no_internet.json"
        JsonFileCode.LOADER -> "lottie/loader.json"
    }

    if (noInternetDialog == null) {
        noInternetDialog = Dialog(this, R.style.FullScreenDialog)
        noInternetDialog?.setContentView(R.layout.dialog_no_internet); noInternetDialog?.setCanceledOnTouchOutside(false); noInternetDialog?.setCancelable(false)
        noInternetDialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
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

fun Activity.getAlertDialog(aMsgText: String, aTitleText: String = getString(R.string.lbl_dialog_title), aPositiveText: String = getString(R.string.lbl_yes), aNegativeText: String = getString(R.string.lbl_no), onPositiveClick: (dialog: DialogInterface, Int) -> Unit, onNegativeClick: (dialog: DialogInterface, Int) -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(aTitleText)
    builder.setMessage(aMsgText)
    builder.setPositiveButton(aPositiveText) { dialog, which ->
        onPositiveClick(dialog, which)
    }
    builder.setNegativeButton(aNegativeText) { dialog, which ->
        onNegativeClick(dialog, which)
    }
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
            onTimerTick(String.format("00 : %d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
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
    callApi(getRestApis().listAllOrders(getUserId().toInt(), page), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_ORDERS, Gson().toJson(it))
        sendOrderCountChangeBroadcast()
        onApiSuccess(it)
    }, onApiError = {
        snackBarError(it)
    }, onNetworkError = {
        noInternetSnackBar()
    })
}

fun AppBaseActivity.createCustomer(requestModel: RequestModel, onApiSuccess: (LoginData) -> Unit) {
    showProgress(true)
    callApi(getRestApis().createCustomer(id = getUserId(), request = requestModel), onApiSuccess = {
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
    })
}

fun AppBaseActivity.createCustomerByEmail(user: FirebaseUser, onApiSuccess: () -> Unit) {
    showProgress(true)
    showProgress(false)

    getSharedPrefInstance().setValue(USER_EMAIL, user.email)
    getSharedPrefInstance().setValue(USER_DISPLAY_NAME, user.displayName.toString())
    var firstName = ""
        var lastName = ""
        if (user.displayName != null && user?.displayName?.split(" ")?.size!! >= 2) {
            firstName = user.displayName?.split(" ")?.get(0)!!
            lastName = user.displayName?.split(" ")?.get(1)!!
        }
        getSharedPrefInstance().setValue(USER_FIRST_NAME, firstName)
        getSharedPrefInstance().setValue(USER_LAST_NAME, lastName)
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

fun AppBaseActivity.signInEmail(user:FirebaseUser, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
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
    saveLoginResponse(user,false,"",onResult,onError)
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

fun AppBaseActivity.saveLoginResponse(it: FirebaseUser,isSocialLogin:Boolean, password: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
    if (true) {
        if (false) {
            showProgress(false)
            onError("Admin is not allowed")
        } else {
            if (it.uid != null) {
                getSharedPrefInstance().setValue(USER_ID, it.uid)
            }
            getSharedPrefInstance().setValue(USER_DISPLAY_NAME, it.displayName)
            getSharedPrefInstance().setValue(USER_EMAIL, it.email)
            getSharedPrefInstance().setValue(USER_NICE_NAME, it.displayName)
            getSharedPrefInstance().setValue(USER_TOKEN, it.providerId)
            if (it.displayName?.isNotEmpty()!!){
                getSharedPrefInstance().setValue(USER_PROFILE, it.displayName)
            }
            getSharedPrefInstance().setValue(IS_SOCIAL_LOGIN,isSocialLogin)
            getSharedPrefInstance().setValue(Constants.SharedPref.USER_PASSWORD, password)

            getSharedPrefInstance().setValue(Constants.SharedPref.SHOW_SWIPE, true)
            var firstName = ""
            var lastName = ""
            if (it.displayName != null && it.displayName?.split(" ")?.size!! >= 2) {
                firstName = it.displayName?.split(" ")?.get(0)!!
                lastName = it.displayName?.split(" ")?.get(1)!!
            } else {
                var userName = it.displayName!!
            }
            getSharedPrefInstance().setValue(USER_FIRST_NAME, firstName)
            getSharedPrefInstance().setValue(USER_LAST_NAME, lastName)
            //getSharedPrefInstance().setValue(USER_ROLE, response.role)
            getSharedPrefInstance().setValue(USER_USERNAME, it.email)
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
    }
}

fun AppBaseActivity.processPayment(requestModel: RequestModel, isContainRedirectUrl: Boolean = true) {
    showProgress(true)
    callApi(getRestApis().processPayment(requestModel), onApiSuccess = {
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
    })
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
    val cartList = Gson().fromJson<ArrayList<CartResponse>>(getSharedPrefInstance().getStringValue(CART_DATA), object : TypeToken<ArrayList<CartResponse>>() {}.type)
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
    return Gson().fromJson<ArrayList<CartResponse>>(getSharedPrefInstance().getStringValue(CART_DATA), object : TypeToken<ArrayList<CartResponse>>() {}.type)
}

fun getWishListFromPref(): ArrayList<WishListData> {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<WishListData>>(getSharedPrefInstance().getStringValue(WISHLIST_DATA), object : TypeToken<ArrayList<WishListData>>() {}.type)
}

fun isExistInWishList(product: ProductDataNew): Boolean {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return false
    }
    val wishList = Gson().fromJson<ArrayList<WishListData>>(getSharedPrefInstance().getStringValue(WISHLIST_DATA), object : TypeToken<ArrayList<WishListData>>() {}.type)
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
    callApi(getRestApis(false).getCart(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_CART_COUNT, it.size); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(it)); sendCartCountChangeBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_CART_COUNT, 0); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(ArrayList<CartResponse>()))
            sendCartCountChangeBroadcast()
        } else {
           // snackBarError(it)
        }
    })
}

fun Activity.fetchAndStoreWishListData() {
    callApi(getRestApis(false).getWishList(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, it.size); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(it)); sendWishListBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, 0); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(ArrayList<WishListData>()))
            sendWishListBroadcast()
        } else {
           // snackBarError(it)
        }
    })
}

fun Activity.addToWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).addWishList(request = requestModel), onApiSuccess = {
        fetchAndStoreWishListData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}

fun Activity.removeFromWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).removeWishList(request = requestModel), onApiSuccess = {
        fetchAndStoreWishListData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}

fun getSlideImagesFromPref(): ArrayList<SliderImagesResponse> {
    if (getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<SliderImagesResponse>>(getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA), object : TypeToken<ArrayList<SliderImagesResponse>>() {}.type)
}

fun getCategoryDataFromPref(): ArrayList<CategoryData> {
    if (getSharedPrefInstance().getStringValue(CATEGORY_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<CategoryData>>(getSharedPrefInstance().getStringValue(CATEGORY_DATA), object : TypeToken<ArrayList<CategoryData>>() {}.type)
}

fun setProductItem(view: View, item: ProductDataNew) {
    view.tvProductName.text = item.name
    if (item.sale_price!!.isNotEmpty()) {
        view.tvDiscountPrice.text = item.sale_price.currencyFormat()
    } else {
        view.tvDiscountPrice.text = item.price?.currencyFormat()
    }
    view.ratingBar.rating = item.average_rating!!.toFloat()
    view.tvOriginalPrice.text = item.regular_price?.currencyFormat()
    view.tvOriginalPrice.applyStrike()
    if (item.full != null) view.ivProduct.loadImageFromUrl(item.full)
}

fun setBannerData(view: View, item: AdDetailsModel.AdsCompleteDetails) {
    view.snackBarError("coooooool")
    if (item.bannerImageUrl != null) view.bannerImage.loadImageFromUrl(item.bannerImageUrl)
}
fun Activity.fetchAndStoreAddressData() {
    callApi(getRestApis(false).getAddress(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_USER_ADDRESS, Gson().toJson(it))
        sendBroadcast(ADDRESS_UPDATE)
        Log.e("response",Gson().toJson(it))
    }, onApiError = {

    }, onNetworkError = {
        noInternetSnackBar()
    })
}
fun Activity.addAddress(address: Address, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).addUpdateAddress(address), onApiSuccess = {
        fetchAndStoreAddressData()
        onSuccess(true)
    }, onApiError = {
        snackBarError(it); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}
fun Activity.removeAddress(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).deleteAddress(request = requestModel), onApiSuccess = {
        fetchAndStoreAddressData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreAddressData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}
fun Activity.changePassword(requestModel: RequestModel, onSuccess: (Boolean) -> Unit){
    callApi(getRestApis().changePassword(getUserId().toInt(), requestModel), onApiSuccess = {
        snackBar(getString(R.string.msg_successpwd));onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })
}

fun Activity.saveProfileImage(requestModel: RequestModel, onSuccess: (Boolean) -> Unit){
    callApi(getRestApis().saveProfileImage( requestModel), onApiSuccess = {
        Log.e("res",it.profile_image)
        getSharedPrefInstance().setValue(USER_PROFILE, it.profile_image)
        onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })
}

@SuppressLint("MissingPermission")
fun Activity.saveLogoImageToStorage(mContext: Context, dbReference: DatabaseReference, storageReference: StorageReference, personalizedBannerBitmap: Bitmap, onSuccess: (Boolean) -> Unit){
    var file = File.createTempFile("image", null, mContext.cacheDir)
    personalizedBannerBitmap.saveAsync(file.path
    ) {
        val ref =
            storageReference.child("uploads/" + getSharedPrefInstance().getStringValue(USER_DISPLAY_NAME))
        val uploadTask = ref.putFile(Uri.fromFile(file))
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
                getSharedPrefInstance().setValue(ADS_BANNER_URL,downloadUri)
                dbReference.child(getSharedPrefInstance().getStringValue(USER_ID)).child("AdvDetails/image").setValue(downloadUri.toString()).addOnCompleteListener {
                    if(task.isSuccessful){
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


fun AppBaseActivity.saveProfileImageToStorage(mContext: Context, dbReference: DatabaseReference, storageReference: StorageReference, personalizedBannerBitmap: Bitmap, onSuccess: (Boolean) -> Unit){
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
    personalizedBannerBitmap.saveAsync(file.path
    ) {
        val ref =
            storageReference.child("uploads/" + getSharedPrefInstance().getStringValue(USER_DISPLAY_NAME)+"_Profile_Pic")
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
                dbReference.child(getSharedPrefInstance().getStringValue(USER_ID)).child("profileExtras/User_Profile").setValue(downloadUri.toString()).addOnCompleteListener {
                    if(task.isSuccessful){
                        onSuccess(true)
                    }
                }
            } else {
                snackBar(task.exception!!.localizedMessage);onSuccess(false)
            }
        }?.addOnFailureListener {
            snackBar(it.localizedMessage);onSuccess(false)
        }
    }
}

fun Activity.addAdvertisement(adDetails: AdDetailsModel.AdDetails, onSuccess: (AdDetailsModel.AdDetails) -> Unit) {
//    callApi(getRestApis(false).addUpdateAddress(adDetails), onApiSuccess = {
//        fetchAndStoreAddressData()
//        onSuccess(true)
//    }, onApiError = {
//        snackBarError(it); onSuccess(false)
//    }, onNetworkError = {
//        noInternetSnackBar(); onSuccess(false)
//    })

    try {
        getSharedPrefInstance().setValue(ADV_DESC,adDetails.adDesc)
        getSharedPrefInstance().setValue(ADV_BRAND,adDetails.adBrandName)
        getSharedPrefInstance().setValue(ADV_NAME,adDetails.adName)
        getSharedPrefInstance().setValue(ADV_TAG,adDetails.adTagline)
        onSuccess(adDetails)
    }
    catch (e:java.lang.Exception){
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
    onApiSuccess: (String) -> Unit
) {

    user.updateEmail(newUserEmail).addOnCompleteListener {
        if(it.isSuccessful){
            showProgress(false)
            getSharedPrefInstance().setValue(USER_EMAIL, user.email)
            onApiSuccess("Email Updated!!!")
            sendProfileUpdateBroadcast()
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }

}

fun AppBaseActivity.updateName(
    user: FirebaseUser,
    displayName: String,
    onApiSuccess: (String) -> Unit
) {
    val profileUpdates = UserProfileChangeRequest.Builder()
        .setDisplayName(displayName)
        .build()
    user.updateProfile(profileUpdates).addOnCompleteListener {
        if(it.isSuccessful){
            showProgress(false)
            getSharedPrefInstance().setValue(USER_DISPLAY_NAME, user.displayName)
            getSharedPrefInstance().setValue(USER_NICE_NAME, user.displayName)
            getSharedPrefInstance().setValue(USER_PROFILE, user.displayName)
            var firstName = ""
            var lastName = ""
            if (user.displayName != null && user.displayName?.split(" ")?.size!! >= 2) {
                firstName = user.displayName?.split(" ")?.get(0)!!
                lastName = user.displayName?.split(" ")?.get(1)!!
            } else {
                var userName = user.displayName!!
            }
            getSharedPrefInstance().setValue(USER_FIRST_NAME, firstName)
            getSharedPrefInstance().setValue(USER_LAST_NAME, lastName)
            onApiSuccess("Name Updated!!!")
            sendProfileUpdateBroadcast()
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }
}

fun AppBaseActivity.updatePhone(
    userId:String,
    dbReference: DatabaseReference,
    phone: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/Phone").setValue(phone).addOnCompleteListener {
        if(it.isSuccessful){
            getSharedPrefInstance().setValue(USER_PHONE, phone)
            onApiSuccess("Phone Updated")
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }

}
fun AppBaseActivity.updateProfileUrl(
    userId:String,
    dbReference: DatabaseReference,
    profileUrl: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/ProfileUrl").setValue(profileUrl).addOnCompleteListener {
        if(it.isSuccessful){
            getSharedPrefInstance().setValue(USER_PROFILE_URL, profileUrl)
            onApiSuccess("Profile Pic Updated")
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }

    }

}

fun AppBaseActivity.updateDOB(
    userId:String,
    dbReference: DatabaseReference,
    dob: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/DOB").setValue(dob).addOnCompleteListener {
        if(it.isSuccessful){
            getSharedPrefInstance().setValue(USER_DOB, dob)
            onApiSuccess("DOB Updated")
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }
}

fun AppBaseActivity.updateORG(
    userId:String,
    dbReference: DatabaseReference,
    org: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/ORG").setValue(org).addOnCompleteListener {
        if(it.isSuccessful){
            getSharedPrefInstance().setValue(USER_ORG,org)

            onApiSuccess("Organization Name Updated")
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }
}

fun AppBaseActivity.updateGender(
    userId:String,
    dbReference: DatabaseReference,
    gender: String,
    onApiSuccess: (String) -> Unit
) {
    dbReference.child(userId).child("profileExtras/Gender").setValue(gender).addOnCompleteListener {
        if(it.isSuccessful){
            getSharedPrefInstance().setValue(USER_GENDER,gender)
            onApiSuccess("Gender Updated")
        }
        else{
            showProgress(false)
            snackBarError(it.exception!!.localizedMessage)
        }
    }
}

