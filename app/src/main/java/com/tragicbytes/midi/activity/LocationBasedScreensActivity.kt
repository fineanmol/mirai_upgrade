package com.tragicbytes.midi.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_location_based_screens.*
import kotlinx.android.synthetic.main.activity_location_based_screens.selectedLocation
import kotlinx.android.synthetic.main.dialog_quantity.*
import kotlinx.android.synthetic.main.toolbar.*

class LocationBasedScreensActivity : AppBaseActivity() {
    private var mQuntity: String = "1"

    private var mLocationScreensAdapter: RecyclerViewAdapter<ProductDataNew>? = null

    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_based_screens)
        setToolbar(toolbar)
        title=getString(R.string.title_location)

        dbReference = FirebaseDatabase.getInstance().reference



        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_quantity)

        // val size = if (stockQuantity == null || stockQuantity >= 5) 5 else stockQuantity
        val size = 5 /** Make size the number of locations available */
        val list: ArrayList<String> = ArrayList()
        for (i in 1..size) {
            list.add("Location $i")
        }
        dialog.listQuantity.adapter =
            ArrayAdapter<String>(this, R.layout.item_quantity, R.id.tvQuantity, list)
        dialog.listQuantity.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                mQuntity = list[position]
                selectedLocation.text = list[position]
                dialog.dismiss()
            }
        selectedLocation.onClick {
            dialog.show()
        }

        rcvScreens.setVerticalLayout()

        setupLocationScreensAdapter()

        dbReference.child("AppData/imageTemplates")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var myBannerList=ArrayList<ProductDataNew>()

                            dataSnapshot.children.forEach {
                                val bannerData =
                                    it.getValue(ProductDataNew::class.java)!!
                                myBannerList.add(bannerData)
                            }
                            shimmerFrameLayout.stopShimmer()
                            shimmerFrameLayout.visibility=View.GONE
                            mLocationScreensAdapter?.addItems(myBannerList)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("Error Occured!")
                    }
                }

            )
    }

    private fun setupLocationScreensAdapter() {
        mLocationScreensAdapter = RecyclerViewAdapter(R.layout.item_screen, onBind = { view, item, position -> setScreenItem(view, item,this) })

        rcvScreens.apply {
            adapter = mLocationScreensAdapter
            rvItemAnimation()
        }
        rcvScreens.adapter = mLocationScreensAdapter

       /* mLocationScreensAdapter?.onItemClick = { pos, view, item ->
            this.showProductDetail(item)
        }*/
    }

    override fun onResume() {
        super.onResume()
        shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmer()
        super.onPause()
    }
}