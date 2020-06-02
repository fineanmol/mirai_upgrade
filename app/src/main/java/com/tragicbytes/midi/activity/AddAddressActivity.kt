package com.tragicbytes.midi.activity

import android.app.Activity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.Address
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.SimpleLocation
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.toolbar.*

class AddAddressActivity : AppBaseActivity(), SimpleLocation.Listener {

    private var address: Address? = null
    private var simpleLocation: SimpleLocation? = null
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        setToolbar(toolbar)
        dbReference = FirebaseDatabase.getInstance().reference


        dbReference = FirebaseDatabase.getInstance().reference

        simpleLocation = SimpleLocation(this)
        simpleLocation?.setListener(this)

        if (intent.hasExtra(Constants.KeyIntent.DATA)) {
            address = intent?.getSerializableExtra(Constants.KeyIntent.DATA) as Address
        }

        if (address != null) {
            title = getString(R.string.lbl_edit_address)
            edtName.setText(address?.first_name)
            edtCountry.setText(address?.country)
            edtCity.setText(address?.city!!)
            edtState.setText(address?.state!!)
            edtPinCode.setText(address?.postcode!!)
            edtAddress1.setText(address?.address_1)
            edtAddress2.setText(address?.address_2)
            edtMobileNo.setText(address?.contact)
        } else {
            title = getString(R.string.lbl_add_new_address)
        }

        btnSaveAddress.onClick {
            if (validate()) {
                showProgress(true)
                if (address == null) {
                    address = Address()
                }
                assignData()

                addAddress(address!!)


            }
        }

        rlUseCurrentLocation.onClick {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), onResult = {
                if (it) {
                    if (isGPSEnable()) {
                        if (isNetworkAvailable()) {
                            showProgress(true)
                            simpleLocation?.beginUpdates()
                        } else {
                            snackBarError(getString(R.string.error_gps_not_enabled))
                        }
                    } else {
                        showGPSEnableDialog()
                    }
                } else {
                    showPermissionAlert(this)
                }
            })
        }
    }

    private fun addAddress(address: Address) {

        try {
            val userID = getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_ID)

            showProgress(true)
            dbReference.child(userID)
                .child("Address").setValue(address)
                .addOnSuccessListener {
                    showProgress(false)
                    snackBar("Address Saved")

                }
                .addOnFailureListener {
                    snackBar(it.message.toString())
                    showProgress(false)

                }
        } catch (e: Exception) {
            snackBar(e.message.toString())
            showProgress(false)
        }

    }


    private fun validate(): Boolean {
        when {
            edtName.checkIsEmpty() -> {
                edtName.showError(getString(R.string.error_field_required))
                edtName.requestFocus()
                return false
            }
            edtAddress1.checkIsEmpty() -> {
                edtAddress1.showError(getString(R.string.error_field_required))
                edtAddress1.requestFocus()
                return false
            }

            edtCity.checkIsEmpty() -> {
                edtCity.showError(getString(R.string.error_field_required))
                edtCity.requestFocus()
                return false
            }
            edtState.checkIsEmpty() -> {
                edtState.showError(getString(R.string.error_field_required))
                edtState.requestFocus()
                return false
            }
            edtCountry.checkIsEmpty() -> {
                edtCountry.showError(getString(R.string.error_field_required))
                edtCountry.requestFocus()
                return false
            }
            edtPinCode.checkIsEmpty() -> {
                edtPinCode.showError(getString(R.string.error_field_required))
                edtPinCode.requestFocus()
                return false
            }
            edtMobileNo.checkIsEmpty() -> {
                edtMobileNo.showError(getString(R.string.error_field_required))
                edtMobileNo.requestFocus()
                return false
            }
            else -> return true
        }
    }

    override fun onPositionChanged() {
        showProgress(false)

        val address = simpleLocation?.address
        if (address != null) {
            edtState.setText(address.adminArea)
            edtPinCode.setText(address.postalCode)
            edtCity.setText(address.locality)
            edtCountry.setText(address.countryName)
            if (address.getAddressLine(0) != null) {
                edtAddress1.setText(address.getAddressLine(0))
            }
            if (address.subAdminArea != null) {
                edtAddress2.setText(address.subLocality)
            }
            simpleLocation?.endUpdates()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private fun assignData() {
        if (address !== null) {
            address?.first_name = edtName.text.toString()
            val name=edtName.text.toString().split(" ")
            if (name.isNotEmpty() && name.size>1){
                address?.first_name = name[0]
                address?.last_name = name[1]
            }
            address?.fullAddress=null
            address?.city = edtCity.text.toString()
            address?.state = edtState.text.toString()
            address?.postcode = edtPinCode.text.toString()
            address?.address_1= edtAddress1.text.toString()
            address?.address_2 = edtAddress2.text.toString()
            address?.country = edtCountry.text.toString()
            address?.contact=edtMobileNo.text.toString()
        }
    }
}