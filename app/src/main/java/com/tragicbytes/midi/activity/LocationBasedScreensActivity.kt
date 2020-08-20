package com.tragicbytes.midi.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.adapter.RecyclerViewAdapter
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.models.ScreensLocationModel
import com.tragicbytes.midi.utils.extensions.*
import kotlinx.android.synthetic.main.activity_location_based_screens.*
import kotlinx.android.synthetic.main.dialog_quantity.*
import kotlinx.android.synthetic.main.toolbar.*

class LocationBasedScreensActivity : AppBaseActivity() {
    private var mQuntity: String = "1"

    private var mLocationScreensAdapter: RecyclerViewAdapter<ScreenDataModel>? = null

    private lateinit var dbReference: DatabaseReference

    private var totalAmount = 0

    private var selectedScreens:ArrayList<ScreenDataModel> = ArrayList()

    private var screensList = ArrayList<ScreenDataModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_based_screens)
        setToolbar(toolbar)
        title = getString(R.string.title_location)

        dbReference = FirebaseDatabase.getInstance().reference

        rcvScreens.setVerticalLayout()

        setupLocationScreensAdapter()

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_quantity)
        showProgress(true)
        val list: ArrayList<String> = ArrayList()
        getAvailableLocationList(list)
        dialog.listQuantity.adapter =
            ArrayAdapter<String>(this, R.layout.item_quantity, R.id.tvQuantity, list)
        dialog.listQuantity.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                shimmerFrameLayout.visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
                mQuntity = list[position]
                selectedLocation.text = list[position]
                fetchLocationBasedScreens(list[position])
                dialog.dismiss()
            }
        selectedLocation.onClick {
            dialog.show()
        }
        sContinue.onClick {
            launchActivity<ConfirmationActivity> {
                putExtra("selectedScreens",selectedScreens)
            }
        }


        setBarChart()

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
                            selectedLocation.text = list[0]
                            fetchLocationBasedScreens(list[0])
                            showProgress(false)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        toast("Unable to fetch locations")
                        showProgress(false)
                    }
                }

            )
    }

    private fun fetchLocationBasedScreens(location: String) {
        selectedScreens.clear()
        totalAmount = 0
        sTotalScreenAmount.text = getString(R.string.RS)+" "+"$totalAmount"
        dbReference.child("AvailableLocations/${location}/screenData")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            screensList = ArrayList()

                            dataSnapshot.children.forEach {
                                val screenData =
                                    it.getValue(ScreenDataModel::class.java)!!
                                screensList.add(screenData)
                            }
                            shimmerFrameLayout.stopShimmer()
                            shimmerFrameLayout.visibility = View.GONE
                            results.text = "RESULTS (${screensList.size})"
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
        mLocationScreensAdapter = RecyclerViewAdapter(
            R.layout.item_screen,
            onBind = { view, item, position -> setScreenItem(view, item, this) })

        rcvScreens.apply {
            adapter = mLocationScreensAdapter
            rvItemAnimation()
        }
        rcvScreens.adapter = mLocationScreensAdapter

        mLocationScreensAdapter?.onItemClick = { pos, view, item ->
            this.selectScreen(view, item, onSelect = {
                totalAmount += it
                selectedScreens.add(screensList[pos])
            },
                onUnSelect = {
                    totalAmount -= it
                    selectedScreens.remove(screensList[pos])
                })
            sTotalScreenAmount.text = "$$totalAmount"
        }
    }


    private fun setBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(8f, 0f))
        entries.add(BarEntry(2f, 1f))
        entries.add(BarEntry(5f, 2f))
        entries.add(BarEntry(20f, 3f))
        entries.add(BarEntry(15f, 4f))
        entries.add(BarEntry(19f, 5f))

        val barDataSet = BarDataSet(entries, "Cells")

        val labels = ArrayList<String>()
        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")
        labels.add("21-Jan")
        labels.add("22-Jan")
        labels.add("23-Jan")


        /* val data = BarData(labels, barDataSet)



         ageBarChart.data = data // set the data and list of lables into chart
        // ageBarChart.description.text="Set Bar Chart Description"

         //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
         barDataSet.color = resources.getColor(R.color.colorAccent)

       ageBarChart.animateY(5000)*/
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


