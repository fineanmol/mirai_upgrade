package com.tragicbytes.midi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.utils.extensions.onClick
import kotlinx.android.synthetic.main.activity_location_based_screens.*
import kotlinx.android.synthetic.main.activity_location_based_screens.selectedLocation
import kotlinx.android.synthetic.main.dialog_quantity.*
import kotlinx.android.synthetic.main.toolbar.*

class LocationBasedScreensActivity : AppBaseActivity() {
    private var mQuntity: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_based_screens)
        setToolbar(toolbar)
        title=getString(R.string.title_location)


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