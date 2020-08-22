package com.tragicbytes.midi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.makeTransparentStatusBar
import io.fabric.sdk.android.services.common.CommonUtils
import kotlinx.android.synthetic.main.activity_my_banner_screen_details.*
import kotlinx.android.synthetic.main.toolbar.*

class MyBannerScreenDetailsActivity : AppBaseActivity() {

    private var screenDataModel=ScreenDataModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_banner_screen_details)

        makeTransparentStatusBar()
        setToolbar(toolbar)
        title = "Screen Details"

        if(intent?.extras?.getSerializable(Constants.KeyIntent.DATA) != null){
            screenDataModel=intent.getSerializableExtra(Constants.KeyIntent.DATA) as ScreenDataModel
            setPieChartData()
            setLineChart()
        }
    }

    private fun setPieChartData() {
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()
        listPie.add(PieEntry(20F, "Pass"))
        listColors.add(resources.getColor(R.color.green))
        listPie.add(PieEntry(50F, "Fail"))
        listColors.add(resources.getColor(R.color.track_yellow))
        listPie.add(PieEntry(30F, "Unanswered"))
        listColors.add(resources.getColor(R.color.colorPrimary))

        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(14F)
        pieChart.data = pieData

        pieChart.setUsePercentValues(true)
        pieChart.isDrawHoleEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun setLineChart(){
        //Part1
        val entries = ArrayList<Entry>()

//Part2
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))

//Part3
        val vl = LineDataSet(entries, "My Type")

//Part4
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.pie2
        vl.fillAlpha = R.color.pie1

//Part5
        lineChart.xAxis.labelRotationAngle = 0f

//Part6
        lineChart.data = LineData(vl)


//Part7
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.axisMaximum = 5F+0.1f

//Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

//Part9
        lineChart.description.text = "Days"
        lineChart.setNoDataText("No forex yet!")

//Part10
        lineChart.animateX(1800, Easing.EaseInExpo)

//Part11
//        val markerView = CustomMarker(this@ShowForexActivity, R.layout.marker_view)
//        lineChart.marker = markerView
    }
}