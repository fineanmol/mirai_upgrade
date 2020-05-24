package com.tragicbytes.midi.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.SignInUpActivity
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtEmail.onFocusChangeListener = this
        edtPassword.onFocusChangeListener = this
        edtPassword.transformationMethod = biggerDotTranformation

        edtEmail.setSelection(edtEmail.length())
        btnSignIn.onClick { if (validate())
            showProgress()
            (activity as SignInUpActivity).doEmailLogin(edtEmail.textToString(),
                edtPassword.textToString()) }
        tvForget.onClick { snackBar(context.getString(R.string.lbl_coming_soon)) }
//        ivFaceBook.onClick {
//            (activity as SignInUpActivity).doFacebookLogin()
//        }
//        ivGoogle.onClick {
//            (activity as SignInUpActivity).doGoogleLogin()
//        }
        btnSignUp.onClick { (activity as SignInUpActivity).loadSignUpFragment() }
        tvForget.onClick { showChangePasswordDialog() }
    }

    private fun validate(): Boolean {
        return when {
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            edtPassword.checkIsEmpty() -> {
                edtPassword.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }
    }

//    private fun doLogin() {
//        (activity as AppBaseActivity).signIn(
//            edtEmail.textToString(),
//            edtPassword.textToString(),
//            onResult = {
//                if (it) activity?.launchActivityWithNewTask<DashBoardActivity>()
//            },
//            onError = {
//                activity?.snackBarError(it)
//            })
//    }

    private fun showChangePasswordDialog() {
        val changePasswordDialog = Dialog(activity!!)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_reset)
        changePasswordDialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        changePasswordDialog.show()
    }
}