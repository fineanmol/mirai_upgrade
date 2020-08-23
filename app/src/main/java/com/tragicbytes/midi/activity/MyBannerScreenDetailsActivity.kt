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
            setPieChartData()
            setLineChart()
            barChart()
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

/*    fun barChart(){
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(1F, 5F))
        entries.add(BarEntry(3F, 7F))
        entries.add(BarEntry(5F, 3F))
        entries.add(BarEntry(7F, 4F))
        entries.add(BarEntry(9F, 1F))
        val dataset = BarDataSet(entries, "First")
        dataset.setColors(*intArrayOf(R.color.pie2, R.color.pie1, R.color.yellow, R.color.green, R.color.colorPrimary))
        val data = BarData(dataset)
        ageWiseChart.data = data
        // replace


        // below is simply styling decisions on code that I have)
        // replace


        // below is simply styling decisions on code that I have)
        val left: YAxis = ageWiseChart.axisLeft
        left.setAxisMaxValue(10f) //dataset.getYMax()+2);

        left.setAxisMinValue(0f)
        ageWiseChart.getAxisRight().setEnabled(false)
        val bottomAxis: XAxis = ageWiseChart.getXAxis()
        bottomAxis.position = XAxis.XAxisPosition.BOTTOM
        bottomAxis.setAxisMinValue(0f)

        bottomAxis.labelCount = 10
        bottomAxis.setAxisMaxValue(10f)
        bottomAxis.setDrawGridLines(false)
        ageWiseChart.setDrawValueAboveBar(false)
//        ageWiseChart.setDescription("")
        // legend
        // legend
        val legend: Legend = ageWiseChart.getLegend()
        legend.yOffset = 40f
//        legend.(Legend.LegendPosition.BELOW_CHART_CENTER)
        legend.textSize = 200f
    }*/
    fun barChart(){
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

/*    val year = ArrayList<lBarDataSet>()

    year.add("2008")
    year.add("2009")
    year.add("2010")
    year.add("2011")
    year.add("2012")
    year.add("2013")
    year.add("2014")
    year.add("2015")
    year.add("2016")
    year.add("2017")*/

    val bardataset = BarDataSet(NoOfEmp, "No Of Employee")
    ageWiseChart.animateY(5000)
    val data = BarData( bardataset)
    bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)
    ageWiseChart.setData(data)
}
}