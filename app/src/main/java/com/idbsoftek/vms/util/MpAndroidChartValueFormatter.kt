package com.idbsoftek.vms.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlin.math.roundToInt

open class MpAndroidChartValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return value.roundToInt().toString()
    }
}