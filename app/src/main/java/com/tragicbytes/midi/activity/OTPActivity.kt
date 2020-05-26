package com.tragicbytes.midi.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.OTPEditText
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.layout_otp.*

class OTPActivity : AppBaseActivity() {

    private var mEds: Array<EditText?>? = null
    private var timer: CountDownTimer? = null
    val user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        emailId.text= user!!.email
        mEds = arrayOf(edDigit1, edDigit2, edDigit3, edDigit4)
        OTPEditText(mEds!!, this,getDrawable(R.color.transparent)!!,getDrawable(R.drawable.bg_unselected_dot)!!)
        mEds!!.forEachIndexed { index, editText ->
            editText?.onFocusChangeListener = focusChangeListener
        }
        timer = startOTPTimer(onTimerTick = {
            tvTimer.text = it
        }, onTimerFinished = {
            tvTimer.hide()
            llResend.show()
        })
        timer?.start()
        tvResend.onClick {
            tvTimer.show()
            llResend.hide()
            timer?.start()

        }
        ivBack.onClick {
            onBackPressed()
        }
    }

    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus)
            (v as EditText).background = getDrawable(R.color.transparent)
    }




}