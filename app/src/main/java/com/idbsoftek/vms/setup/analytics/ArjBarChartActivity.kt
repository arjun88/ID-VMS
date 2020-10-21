package com.idbsoftek.vms.setup.analytics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idbsoftek.vms.R

class ArjBarChartActivity : AppCompatActivity() {

    private var barChartRV: RecyclerView? = null
    private var yAxisRV: RecyclerView? = null
    private var xAxisRV: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arj_bar_chart)

        initBarChart()
    }

    private fun initBarChart(){
        barChartRV = findViewById(R.id.bar_chart_rv)
        val layMgr = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        barChartRV!!.layoutManager = layMgr
        barChartRV!!.setHasFixedSize(true)

//        Y-AXIS
        yAxisRV = findViewById(R.id.y_axis_rv)
        val yAxisLayMgr = LinearLayoutManager(this)
        yAxisRV!!.layoutManager = yAxisLayMgr
        yAxisRV!!.setHasFixedSize(true)

        val adapter = YAxisLabelAdapter()
        yAxisRV!!.adapter = adapter

    }


}
