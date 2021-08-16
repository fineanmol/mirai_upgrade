package com.tragicbytes.midi.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.toolbar.*

class HelpActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        title = getString(R.string.title_help)
        setToolbar(toolbar)

        btnSubmit.onClick {
            when {
                validate() -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.miraivizion_gmail_com), null))
                    emailIntent.putExtra(Intent.EXTRA_TEXT, edtDescription.text.toString())
                    startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    finish()
                }
            }
        }

    }




    private fun validate(): Boolean {
        return when {
            edtContact.checkIsEmpty() -> {
                edtContact.showError(getString(R.string.error_field_required))
                false
            }
            !edtContact.isValidPhoneNumber() -> {
                edtContact.showError(getString(R.string.error_enter_valid_contact))
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
            edtDescription.checkIsEmpty() -> {
                edtDescription.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }
    }

}
