package com.idbsoftek.vms.util

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class YAxisLabelFormatter: ValueFormatter() {
    fun getIntValueForYAxis(value: Float) : String{
        return value.roundToInt().toString()
    }
}