package com.idbsoftek.vms.util

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieDataSet

class ChartDisplayer {
    private var barChart: BarChart? = null
    private var labels: List<String>? = null
    private var barDataSet: BarDataSet? = null
    private var pieChart: PieChart? = null
    private var pieDataSet: PieDataSet? = null
    private var desc: String? = null

    constructor(pieChart: PieChart?, pieDataSet: PieDataSet?, desc: String?) {
        this.pieChart = pieChart
        this.pieDataSet = pieDataSet
        this.desc = desc
    }

    constructor(
        barChart: BarChart?,
        labels: List<String>?,
        barDataSet: BarDataSet?
    ) {
        this.barChart = barChart
        this.labels = labels
        this.barDataSet = barDataSet
    }

    fun displayBarChart() {
        val barData = BarData()
        barData.addDataSet(barDataSet)
        barData.barWidth = 0.5f
        barChart!!.data = barData
        val description =
            Description()
        description.text = ""
        barChart!!.description = description
        val yLeft = barChart!!.axisLeft
        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.axisMaximum = 100f
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = true
        //Set label count to 5 as we are displaying 5 star rating
//
        val yRight = barChart!!.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false
        barChart!!.legend.isEnabled = false
        //Hide Grid Lines
//  monthlyYearlyChart.getAxisLeft().setDrawAxisLine(false);
        barChart!!.axisLeft.setDrawGridLines(false)
        barChart!!.axisRight.setDrawGridLines(false)
        barChart!!.xAxis.setDrawGridLines(false)
        val xAxis = barChart!!.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        //To set X-axis Labels At Bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // CENTER align X axis Labels
        xAxis.axisMinimum = barData.xMin - .5f
        xAxis.axisMaximum = barData.xMax + .5f
        xAxis.labelCount = labels!!.size
        barChart!!.animateXY(3000, 3000)
        //Zoom
        barChart!!.isDoubleTapToZoomEnabled = false
        barChart!!.setPinchZoom(false)
        barChart!!.invalidate()
    }

    fun displayBarChart(maxYAxis: Int) {
        val barData = BarData()
        barData.addDataSet(barDataSet)
        barData.barWidth = 0.5f
        barChart!!.data = barData
        val description =
            Description()
        description.text = ""
        barChart!!.description = description
        val yLeft = barChart!!.axisLeft
        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.axisMaximum = maxYAxis.toFloat()
        yLeft.axisMinimum = 0f
        yLeft.isEnabled = true
        yLeft.granularity = 1.0f
        yLeft.isGranularityEnabled = true
        val yRight = barChart!!.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false
        barChart!!.legend.isEnabled = false
        //Hide Grid Lines
//  monthlyYearlyChart.getAxisLeft().setDrawAxisLine(false);
        barChart!!.axisLeft.setDrawGridLines(false)
        barChart!!.axisRight.setDrawGridLines(false)
        barChart!!.xAxis.setDrawGridLines(false)
        val xAxis = barChart!!.xAxis
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        //To set X-axis Labels At Bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // CENTER align X axis Labels
        xAxis.axisMinimum = barData.xMin - .5f
        xAxis.axisMaximum = barData.xMax + .5f
        xAxis.labelCount = labels!!.size
        xAxis.textSize = 6f
        barChart!!.animateXY(3000, 3000)
        //Zoom
        barChart!!.isDoubleTapToZoomEnabled = false
        barChart!!.setPinchZoom(false)
        //    barChart.setHorizontalScrollBarEnabled(true);
//        barChart.setVisibleXRangeMaximum(10); // allow 20 values to be displayed at once on the x-axis, not more
//        barChart.moveViewToX(5);
        barChart!!.invalidate()
    }
}