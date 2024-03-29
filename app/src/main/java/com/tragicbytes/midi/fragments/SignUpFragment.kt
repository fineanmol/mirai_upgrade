package com.tragicbytes.midi.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tragicbytes.midi.R
import com.tragicbytes.midi.activity.SignInUpActivity
import com.tragicbytes.midi.models.RequestModel
import com.tragicbytes.midi.models.UserDetailsModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFragment()
    }

    private fun initializeFragment() {
        edtEmail.onFocusChangeListener = this
        edtPassword.onFocusChangeListener = this
        edtConfirmPassword.onFocusChangeListener = this
        edtFirstName.onFocusChangeListener = this
        edtLastName.onFocusChangeListener = this
        edtPassword.transformationMethod = biggerDotTranformation
        edtConfirmPassword.transformationMethod = biggerDotTranformation

        btnSignUp.onClick { if (validate()) createCustomer() }
        btnSignIn.onClick { (activity as SignInUpActivity).loadSignInFragment() }
        terms_and_Condition_Btn.onClick { activity?.openCustomTab("https://www.nightowldevelopers.com/terms-and-conditions.php") }
    }

    private fun createCustomer() {
        showProgress()
        val requestModel = RequestModel()
        requestModel.email = edtEmail.textToString()
        requestModel.first_name = edtFirstName.textToString()
        requestModel.last_name = edtLastName.textToString()
        requestModel.password = edtPassword.textToString()
        requestModel.username=edtFirstName.textToString()
        /////////
        val userPersonalDetails=UserDetailsModel().userPersonalDetails
        userPersonalDetails.email = edtEmail.textToString()
        userPersonalDetails.firstName = edtFirstName.textToString()
        userPersonalDetails.lastName = edtLastName.textToString()
        userPersonalDetails.password = edtPassword.textToString()
        (activity as SignInUpActivity).addUser(userPersonalDetails)
    }

    private fun validate(): Boolean {
        return when {
            edtFirstName.checkIsEmpty() -> {
                edtFirstName.showError(getString(R.string.error_field_required))
                false
            }
            edtLastName.checkIsEmpty() -> {
                edtLastName.showError(getString(R.string.error_field_required))
                false
            }
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            !edtEmail.isValidEmail() -> {
                edtEmail.showError(getString(R.string.error_enter_valid_email))
                false
            }
            edtPassword.checkIsEmpty() -> {
                edtPassword.showError(getString(R.string.error_field_required))
                false
            }
            edtConfirmPassword.checkIsEmpty() -> {
                edtConfirmPassword.showError(getString(R.string.error_field_required))
                false
            }
            !edtPassword.text.toString().equals(edtConfirmPassword.text.toString(), false) -> {
                edtConfirmPassword.showError(getString(R.string.error_password_not_matches))
                false
            }
            else -> true
        }
    }
}