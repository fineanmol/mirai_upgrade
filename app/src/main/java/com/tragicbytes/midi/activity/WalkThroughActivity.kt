package com.tragicbytes.midi.activity

import android.os.Bundle
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.WalkAdapter
import com.tragicbytes.midi.utils.CarouselEffectTransformer
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.Constants.SharedPref.SHOW_SWIPE
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_walk_through.*

class WalkThroughActivity : AppBaseActivity() {
    private var mCount: Int? = null
    private var mHeading = arrayOf("Hi, We are Mirai Technologies!", "Advertise on BillBoard!", "Effective Communications!")
    private val mSubHeading = arrayOf("We make around your city Affordable,\n easy and efficient.", "By Choosing us, you can advertise \non high traffic Billboards.", "Out of All, Digital Advertising is the most\n effective way to promote your brand")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)

        init()
        val adapter = WalkAdapter()

        ViewPager.adapter = adapter

        dots.attachViewPager(ViewPager)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        mCount = adapter.count

        btnStatShopping.onClick {
            getSharedPrefInstance().setValue(SHOW_SWIPE, true)
            launchActivity<SignInUpActivity>{
                putExtra(Constants.KeyIntent.LOGIN, "FALSE")
            }
        }
        llSignIn.onClick {
            getSharedPrefInstance().setValue(SHOW_SWIPE, true)
            launchActivity<SignInUpActivity>{
                putExtra(Constants.KeyIntent.LOGIN, "TRUE")
            }
        }
    }

    private fun init() {
        ViewPager.apply {
            clipChildren = false
            pageMargin = resources.getDimensionPixelOffset(R.dimen.spacing_small)
            offscreenPageLimit = 3
            setPageTransformer(false, CarouselEffectTransformer(this@WalkThroughActivity))
            offscreenPageLimit = 0

            onPageSelected { position: Int ->
                val animFadeIn = android.view.animation.AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                tvHeading.startAnimation(animFadeIn)
                tvSubHeading.startAnimation(animFadeIn)
                tvHeading.text = mHeading[position]
                tvSubHeading.text = mSubHeading[position]
            }
        }
    }
}