package com.idbsoftek.vms.setup.analytics

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.setup.visitor_stats.DashboardListItem
import com.idbsoftek.vms.setup.visitor_stats.DbVisitorStatsApiResponse
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.ChartDisplayer
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt


class DeptStatusWiseAnalytics : VmsMainActivity(), TokenRefreshable,
    AdapterView.OnItemSelectedListener {
    private var deptBarChart: BarChart? = null
    private var statusPieChart: PieChart? = null
    private var activity: Activity? = null
    private var prefUtil: PrefUtil? = null

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    private var fromDate: String? = ""
    private var toDate: String? = ""

    private var deptSpinner: AppCompatSpinner? = null
    private var deptSpinnerDD: ArrayList<String> = ArrayList()
    private var deptSpinnerList: ArrayList<DashboardListItem> = ArrayList()

    private var deptSel = ""
    private var statusWiseDeptSel: DashboardListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dept_status_wise_analytics)
        setActionBarTitle(
            "Analytics"
        )

        activity = this
        prefUtil = PrefUtil(activity!!)

        fromDate = "2020-10-16"
        toDate = "2020-10-17"

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        deptBarChart = findViewById(R.id.dept_bar_chart)
        statusPieChart = findViewById(R.id.status_pie_chart)
        deptSpinner = findViewById(R.id.dept_spinner_analytics)

    }

    override fun onStart() {
        super.onStart()
        getDeptAnalyticsApi()
    }

    @SuppressLint("DefaultLocale")
    private fun setDeptDD() {
        deptSpinnerDD.clear()
        for (i in 0 until deptSpinnerList.size) {
            val name = deptSpinnerList[i].departmentName!!.toLowerCase().capitalize()

            deptSpinnerDD.add(name)
        }

        val adapter = ArrayAdapter(
            activity!!,
            android.R.layout.simple_spinner_item, deptSpinnerDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        deptSpinner!!.adapter = adapter
        deptSpinner!!.onItemSelectedListener = this
    }


    private fun getDeptAnalyticsApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${PrefUtil.getBaseUrl()}Stats/DepartmentWiseVisitCountApi"
        tokenRefreshSel = this
        // DEPT ANALYTICS
        apiCallable.loadVisitorStatsAdmin(
            url, prefUtil.getApiToken(), fromDate, toDate
        ).enqueue(object : Callback<DbVisitorStatsApiResponse> {
            override fun onResponse(
                call: Call<DbVisitorStatsApiResponse>,
                response: Response<DbVisitorStatsApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        val visitorLogApiResponse = response.body()
                        if (visitorLogApiResponse!!.status == true) {
                            if (visitorLogApiResponse.dashboardList != null) {
                                if (visitorLogApiResponse.dashboardList.isNotEmpty()) {

                                    deptSpinnerList.clear()
                                    deptSpinnerList.addAll(visitorLogApiResponse.dashboardList)
                                    //deptSpinnerList = visitorLogApiResponse.dashboardList as ArrayList<DashboardListItem>
                                    setDeptDD()
                                    // setData(visitorLogApiResponse.dashboardList)
                                    showBarChartForDeptAnalytics(
                                        visitorLogApiResponse.dashboardList
                                    )
                                } else {
                                    showToast("No Data Found!")
                                }
                            } else {
                                showToast("No Data Found!")
                            }
                            afterLoad()
                        } else {
                            afterLoad()
                            showToast(response.body()!!.message!!)
                        }
                    }
                    response.code() == 401 -> {
                        afterLoad()
                        tokenRefresh!!.doTokenRefresh(
                            activity!!, tokenRefreshSel
                        )
                        // ****
                    }
                    response.code() == 500 -> {
                        afterLoad()
                    }
                }
            }

            override fun onFailure(call: Call<DbVisitorStatsApiResponse>, t: Throwable) {
                t.printStackTrace()
                afterLoad()
            }
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }


    private fun onLoad() {
        /*deptRV!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE*/
    }

    private fun afterLoad() {
        /*deptRV!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE*/
    }

    private fun getXAxisValues(deptList: List<DashboardListItem>): ArrayList<String>? {
        val xAxis = ArrayList<String>()

        for (dept in deptList) {
            xAxis.add(dept.departmentName!!)
        }
        /* xAxis.add("JAN")
         xAxis.add("FEB")
         xAxis.add("MAR")
         xAxis.add("APR")
         xAxis.add("MAY")
         xAxis.add("JUN")*/
        return xAxis
    }

    private fun showBarChartForDeptAnalytics(deptList: List<DashboardListItem>) {
        val barEntries = java.util.ArrayList<BarEntry>()

        for (i in deptList.indices) {
            if (deptList[i].totalCount!! > 0) {
                val floatIndex = i.toFloat()
                barEntries.add(
                    BarEntry(
                        floatIndex, deptList[i].totalCount!!.toFloat()
                    )
                )
            }
        }

        var label = "Monthly"

        val barDataSets = BarDataSet(barEntries, label)
        // barDataSets.colors = ColorTemplate.MATERIAL_COLORS
        barDataSets.setColors(*ColorTemplate.MATERIAL_COLORS)
        // barDataSets.valueFormatter = IntValueFormatter()
        val surveys: MutableList<String> = java.util.ArrayList()
        var maxValueOfBarChartYAxis = 0
        Log.e("Selected Cat Month", ": " + deptList.size)
        for (surveyInCat in deptList) {
            //if (surveyInCat.getCount() > 0) {
            surveys.add(surveyInCat.departmentName!!)
            if (maxValueOfBarChartYAxis < surveyInCat.totalCount!!)
                maxValueOfBarChartYAxis = surveyInCat.totalCount
            //  }
        }
        maxValueOfBarChartYAxis += 1
        deptBarChart!!.axisLeft.axisMaximum = maxValueOfBarChartYAxis.toFloat()

        /* val yAxis = deptBarChart!!.axisLeft
         yAxis.valueFormatter = IndexAxisValueFormatter()*/

        /* BarData monthlyYearlyBarData = new BarData();
        monthlyYearlyBarData.addDataSet(barDataSets);*/

        val xAxis = deptBarChart!!.xAxis
        xAxis.valueFormatter = IAxisValueFormatter { value,
                                                     axis ->
            value.roundToInt().toString()
        }

        val chartDisplayer = ChartDisplayer(
            deptBarChart, getXAxisValues(deptList),
            barDataSets
        )

        val barData = BarData()
        barData.addDataSet(barDataSets)
        barData.barWidth = 0.5f

        chartDisplayer.displayBarChart(maxValueOfBarChartYAxis)
    }

    override fun onTokenRefresh(responseCode: Int, token: String) {
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(activity!!)
            }
            200 -> {
                getDeptAnalyticsApi()
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == deptSpinner) {
            deptSel = deptSpinnerList[position].deptCode!!

            for (dept in deptSpinnerList) {
                if (dept.deptCode == deptSel) {
                    statusWiseDeptSel = dept
                    break
                }
            }

            if (statusWiseDeptSel != null) {
                showPieChartForAllStatus()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    private fun showPieChartForAllStatus() {

        val pieEntries = java.util.ArrayList<PieEntry>()
        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.pending!!.toFloat(),
                "Pending"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.approved!!.toFloat(),
                "Approved"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.checkIn!!.toFloat(),
                "Checked In"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.sessionOut!!.toFloat(),
                "Session Out"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.meetInProgress!!.toFloat(),
                "Meet In Progress"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.meetCompleted!!.toFloat(),
                "Meet Completed"
            )
        )

        pieEntries.add(
            PieEntry(
                statusWiseDeptSel!!.checkOut!!.toFloat(),
                "Checked Out"
            )
        )

        val pieDataSet = PieDataSet(pieEntries, "")
        val chartDisplayer = ChartDisplayer(
            statusPieChart,
            pieDataSet, "Status-wise count"
        )

        //  pieChartOfCategories.setMaxAngle(pieMaxValue);
        chartDisplayer.displayPieChart()
    }

}