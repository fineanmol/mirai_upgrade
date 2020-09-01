package com.tragicbytes.midi.utils.extensions

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class XAxisValueFormatter(private val values: Array<String>) : IAxisValueFormatter {

        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            // "value" represents the position of the label on the axis (x or y)
            return values[value.toInt()]
        }
}




