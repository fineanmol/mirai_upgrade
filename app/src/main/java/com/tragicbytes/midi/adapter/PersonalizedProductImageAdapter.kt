package com.tragicbytes.midi.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.tragicbytes.midi.R
import kotlinx.android.synthetic.main.layout_itemimage.view.*

class PersonalizedProductImageAdapter(private var mImg: ArrayList<Bitmap>) : PagerAdapter() {

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_itemimage, parent, false)

        view.imgSlider.setImageBitmap(mImg[position])

        parent.addView(view)
        return view
    }

    override fun isViewFromObject(v: View, `object`: Any): Boolean = v === `object` as View

    override fun getCount(): Int = mImg!!.size

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) = parent.removeView(`object` as View)

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
    


}