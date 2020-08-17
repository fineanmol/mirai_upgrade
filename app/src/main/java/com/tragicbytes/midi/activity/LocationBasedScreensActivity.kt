package com.tragicbytes.midi.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ProductDataNew
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.models.ScreensLocationModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_location_based_screens.*
import kotlinx.android.synthetic.main.activity_location_based_screens.selectedLocation
import kotlinx.android.synthetic.main.dialog_quantity.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.log

class LocationBasedScreensActivity : AppBaseActivity() {
    private var mQuntity: String = "1"

    private var mLocationScreensAdapter: RecyclerViewAdapter<ScreenDataModel>? = null

    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_based_screens)
        setToolbar(toolbar)
        title=getString(R.string.title_location)

        dbReference = FirebaseDatabase.getInstance().reference

        rcvScreens.setVerticalLayout()

        setupLocationScreensAdapter()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_quantity)

        val list: ArrayList<String> = ArrayList()
        getAvailableLocationList(list)
        dialog.listQuantity.adapter =
            ArrayAdapter<String>(this, R.layout.item_quantity, R.id.tvQuantity, list)
        dialog.listQuantity.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                shimmerFrameLayout.visibility=View.VISIBLE
                shimmerFrameLayout.startShimmer()
                mQuntity = list[position]
                selectedLocation.text = list[position]
                fetchLocationBasedScreens(list[position])
                dialog.dismiss()
            }
        selectedLocation.onClick {
            dialog.show()
        }

    }

    private fun getAvailableLocationList(list: ArrayList<String>) {
        dbReference.child("AvailableLocations")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.children.forEach {
                                list.add(it.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("Unable to fetch locations")
                    }
                }

            )
    }

    private fun fetchLocationBasedScreens(location: String) {
        var screensLocationModel=ScreensLocationModel()
        var yzx=screensLocationModel.screenData
        dbReference.child("AvailableLocations/${location}/screenData")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            var screensList=ArrayList<ScreenDataModel>()

                            dataSnapshot.children.forEach {
                                val screenData =
                                    it.getValue(ScreenDataModel::class.java)!!
                                screensList.add(screenData)
                            }
                            shimmerFrameLayout.stopShimmer()
                            shimmerFrameLayout.visibility=View.GONE
                            mLocationScreensAdapter?.addItems(screensList)

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("Unable to fetch locations")
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