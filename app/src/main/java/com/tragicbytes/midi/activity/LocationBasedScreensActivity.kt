package com.tragicbytes.midi.activity

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
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
        /** Close the expand menu at first time*/
        expandableLayout.visibility = View.GONE
        expandBtn.setOnClickListener {
            if (expandableLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                expandBtn.rotation=180F
            } else {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.GONE
                expandBtn.rotation= 360F

            }
            pieChart.animateY(1500, Easing.EaseInBounce)
        }

        pieChart.setUsePercentValues(true)
        val xvalues = ArrayList<PieEntry>()
        xvalues.add(PieEntry(60.0f, "Male"))
        xvalues.add(PieEntry(40.0f, "Female"))
        val dataSet = PieDataSet(xvalues, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter())

        pieChart.data = data
        dataSet.setColors(
            intArrayOf(R.color.pie1, R.color.pie2),
            this
        )
        dataSet.sliceSpace = 5f
        pieChart.description.text = "Gender Ratio"
        pieChart.description.textSize=12f
        pieChart.isDrawHoleEnabled = false
        data.setValueTextSize(13f)


        chartDetails(pieChart, Typeface.SANS_SERIF)
    }

    fun chartDetails(mChart: PieChart, tf: Typeface) {
        mChart.description.isEnabled = true
        mChart.centerText = ""
        mChart.setCenterTextSize(10F)
        mChart.setCenterTextTypeface(tf)
        val l = mChart.legend
        mChart.legend.isWordWrapEnabled = true
        mChart.legend.isEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.formSize = 20F
        l.formToTextSpace = 5f
        l.form = Legend.LegendForm.DEFAULT
        l.textSize = 12f
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.isWordWrapEnabled = true
        l.setDrawInside(true)
        mChart.setTouchEnabled(true)
        mChart.setDrawEntryLabels(true)
        mChart.legend.isWordWrapEnabled = true
        mChart.setExtraOffsets(0f, 0f, 20f, 0f)
        mChart.setUsePercentValues(true)
        // mChart.rotationAngle = 0f
        mChart.setUsePercentValues(true)
        mChart.setDrawCenterText(true)
        mChart.description.isEnabled = true
        mChart.isRotationEnabled = true
    }

//    override fun onResume() {
//        super.onResume()
//        shimmerFrameLayout.startShimmer()
//    }
//
//    override fun onPause() {
//        shimmerFrameLayout.stopShimmer()
//        super.onPause()
//    }
}