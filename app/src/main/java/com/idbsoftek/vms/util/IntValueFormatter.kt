package com.idbsoftek.vms.util

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.roundToInt

class IntValueFormatter : MpAndroidChartValueFormatter(), IValueFormatter {
    override fun getFormattedValue(
        value: Float,
        entry: Entry,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler
    ): String {
        return value.roundToInt().toString()
    }
}