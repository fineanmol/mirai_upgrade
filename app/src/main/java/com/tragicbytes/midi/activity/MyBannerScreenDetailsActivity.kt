package com.tragicbytes.midi.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.tragicbytes.midi.AppBaseActivity
import com.tragicbytes.midi.R
import com.tragicbytes.midi.models.AgeGroupDetail
import com.tragicbytes.midi.models.ScreenDataModel
import com.tragicbytes.midi.utils.Constants
import com.tragicbytes.midi.utils.extensions.makeTransparentStatusBar
import kotlinx.android.synthetic.main.activity_my_banner_screen_details.*
import kotlinx.android.synthetic.main.toolbar.*


class MyBannerScreenDetailsActivity : AppBaseActivity() {

    lateinit var skillRatingChart : HorizontalBarChart

    private var screenDataModel = ScreenDataModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_banner_screen_details)

        makeTransparentStatusBar()
        setToolbar(toolbar)
        title = "Screen Details"

        if (intent?.extras?.getSerializable(Constants.KeyIntent.DATA) != null) {
            screenDataModel =
                intent.getSerializableExtra(Constants.KeyIntent.DATA) as ScreenDataModel
            setPieChartData(screenDataModel)
//            setBarChart(screenDataModel.screenAgeGroupPref)
//            barChart(screenDataModel)
            setSkillGraph(screenDataModel.screenAgeGroupPref)
            activeTime.text= screenDataModel.screenActiveTime.toString()
            location.text= screenDataModel.screenLocation
            city.text= screenDataModel.screenCity
            comment.text= screenDataModel.screenAdminComment


        }
    }

/*    private fun setBarChart(screenAgeGroupPref: AgeGroupDetail) {
        val labels = arrayListOf(
            "Below18", "18-35", "35-50",
            "Above50"
        )

        ageWiseChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
//        ageWiseChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        ageWiseChart.setDrawGridBackground(false)
        ageWiseChart.axisLeft.isEnabled = false
        ageWiseChart.axisRight.isEnabled = false
        ageWiseChart.description.isEnabled = false

        val entries = arrayListOf(
            BarEntry(0f, screenAgeGroupPref.generationZ.toFloat()),
            BarEntry(1f, screenAgeGroupPref.generationY.toFloat()),
            BarEntry(2f, screenAgeGroupPref.generationX.toFloat()),
            BarEntry(3f, screenAgeGroupPref.babyBoomers.toFloat())
        )
        val set = BarDataSet(entries, "BarDataSet")
        set.valueTextSize = 12f

        ageWiseChart.data = BarData(set)
        ageWiseChart.invalidate()

        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        set.color = resources.getColor(R.color.colorAccent)

        ageWiseChart.animateY(5000)
    }*/

    private fun setPieChartData(screenDataModel: ScreenDataModel) {

        var maleValue = screenDataModel.screenGenderRatio.toFloat()
        val listPie = ArrayList<PieEntry>()
        val listColors = ArrayList<Int>()
        listPie.add(PieEntry(maleValue, "Male"))
        listColors.add(resources.getColor(R.color.pie1))
        listPie.add(PieEntry(100 - maleValue, "Female"))
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
        pieChart.animateY(1400, Easing.EasingOption.EaseInBounce)
    }

    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    fun setSkillGraph(screenAgeGroupPref: AgeGroupDetail) {

        skillRatingChart = ageWiseChart

        skillRatingChart.setDrawBarShadow(false)
        val description = Description()
        description.text = ""
        skillRatingChart.description = description
        skillRatingChart.legend.setEnabled(false)
        skillRatingChart.setPinchZoom(false)
        skillRatingChart.setDrawValueAboveBar(false)

        //Display the axis on the left (contains the labels 1*, 2* and so on)
        val xAxis = skillRatingChart.getXAxis()
        xAxis.setDrawGridLines(false)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setEnabled(true)
        xAxis.setDrawAxisLine(false)


        val yLeft = skillRatingChart.axisLeft

//Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.axisMaximum = 100f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = false

        //Set label count to 5 as we are displaying 5 star rating
        xAxis.labelCount = 4

//Now add the labels to be added on the vertical axis
        val values = arrayOf("50+", "45-50", "18-34", "Below 18")
        xAxis.valueFormatter = XAxisValueFormatter(values)

        val yRight = skillRatingChart.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false

        //Set bar entries and add necessary formatting
        setGraphData(screenAgeGroupPref)

        //Add animation to the graph
        skillRatingChart.animateY(2000,Easing.EasingOption.EaseInBounce)
    }

    class XAxisValueFormatter(private val values: Array<String>) :
        IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            // "value" represents the position of the label on the axis (x or y)
            return values[value.toInt()]
        }

    }

    private fun setGraphData(screenAgeGroupPref: AgeGroupDetail) {

        //Add a list of bar entries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, screenAgeGroupPref.generationZ.toFloat()))
        entries.add(BarEntry(1f, screenAgeGroupPref.generationY.toFloat()))
        entries.add(BarEntry(2f, screenAgeGroupPref.generationX.toFloat()))
        entries.add(BarEntry(3f, screenAgeGroupPref.babyBoomers.toFloat()))

        //Note : These entries can be replaced by real-time data, say, from an API

        val barDataSet = BarDataSet(entries, "Bar Data Set")

        //Set the colors for bars with first color for 1*, second for 2* and so on
        barDataSet.setColors(
            ContextCompat.getColor(skillRatingChart.context, R.color.green),
            ContextCompat.getColor(skillRatingChart.context, R.color.pie1),
            ContextCompat.getColor(skillRatingChart.context, R.color.pie2),
            ContextCompat.getColor(skillRatingChart.context, R.color.track_yellow)
        )

        //Set bar shadows
        ageWiseChart.setDrawBarShadow(true)
//        barDataSet.barShadowColor = Color(40, 150, 150, 150)
        val data = BarData(barDataSet)

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.barWidth = 0.9f

        //Finally set the data and refresh the graph
        ageWiseChart.data = data
        ageWiseChart.invalidate()

    }

}