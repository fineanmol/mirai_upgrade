package com.tragicbytes.midi.activity

import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.makeTransparentStatusBar
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
            setPieChartData(screenDataModel)
           // setLineChart()
            barChart(screenDataModel)
        }
    }

    private fun setPieChartData(screenDataModel: ScreenDataModel) {
        var maleValue = screenDataModel.screenGenderRatio.toFloat()

        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()
        listPie.add(PieEntry(maleValue, "Male"))
        listColors.add(resources.getColor(R.color.pie1))
        listPie.add(PieEntry(100-maleValue, "Female"))
        listColors.add(resources.getColor(R.color.pie2))


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

    fun barChart(screenDataModel: ScreenDataModel) {
    val NoOfEmp = ArrayList<BarEntry>()

    NoOfEmp.add(BarEntry(945f, 0f))
    NoOfEmp.add(BarEntry(1040f, 1f))
    NoOfEmp.add(BarEntry(1133f, 2f))
    NoOfEmp.add(BarEntry(1240f, 3f))
    NoOfEmp.add(BarEntry(1369f, 4f))
    NoOfEmp.add(BarEntry(1487f, 5f))
    NoOfEmp.add(BarEntry(1501f, 6f))
    NoOfEmp.add(BarEntry(1645f, 7f))
    NoOfEmp.add(BarEntry(1578f, 8f))
    NoOfEmp.add(BarEntry(1695f, 9f))



    val bardataset = BarDataSet(NoOfEmp, "No Of Employee")
    ageWiseChart.animateY(5000)
    val data = BarData( bardataset)
    bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)
    ageWiseChart.setData(data)
}
}