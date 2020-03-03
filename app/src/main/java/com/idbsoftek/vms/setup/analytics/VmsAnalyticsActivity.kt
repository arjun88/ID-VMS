package com.idbsoftek.vms.setup.analytics

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VmsAnalyticsActivity : VmsMainActivity(), DateTimeSelectable {
    var filterBtn: FloatingActionButton? = null

    // Chart
    private var barChart: BarChart? = null

    private var numOfVisitorsTV: AppCompatTextView? = null
    private var numOfVisTitleTV: AppCompatTextView? = null

    private var loader: ProgressBar? = null

    private var isFromDeptScreen: Boolean? = false
    private var isFromFilter: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vms_analytics)

        setActionBarTitle("Analytics")

        barChart = findViewById(R.id.analytics_bar_chart)

        loader = findViewById(R.id.analytics_db_progress)
        filterBtn = findViewById(R.id.filter_btn_analytics)
        numOfVisitorsTV = findViewById(R.id.num_of_visitors_count_tv_dashboard)
        numOfVisTitleTV = findViewById(R.id.num_of_vis_title_tv)
        numOfVisitorsTV!!.text = ""
        numOfVisTitleTV!!.text = "Today"

        afterLoad()

        isFromDeptScreen = false

        filterBtn!!.setOnClickListener {
            showFilterPopUp(false)
        }

        barChart!!.visibility = View.VISIBLE

        setBarChart()
    }

    private fun onLoad() {
        loader!!.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        loader!!.visibility = View.GONE
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        getDashboardAnalyticsApi()
    }

    //API

    private fun setDashboard() {
        findViewById<MaterialCardView>(R.id.visitor_count_cv).setOnClickListener {
            val numOfVisitors = numOfVisitorsTV!!.text.toString()
            if (numOfVisitors.isNotEmpty() && numOfVisitors != "NA" && numOfVisitors != "0") {
                if (numOfVisTitleTV!!.text == "Today") {
                    isFromFilter = false
                }
                moveToFragment(
                    true,
                    isFromFilter = isFromFilter!!,
                    deptList = deptListFromFilter,
                    fromDate = fromDateSel!!,
                    toDate = toDateSel!!
                )
            }
            else{
                Log.e("NUM_OF_VIS_TV: " , "${numOfVisitors}")
            }
        }
    }

    private fun getDashboardAnalyticsApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}DepartmentAnalytics"


        apiCallable.loadDashboardForAnalytics(
            url, prefUtil.userName, prefUtil.sessionID
        )
            .enqueue(object : Callback<AnalyticsDashboardApiResponse> {
                override fun onResponse(
                    call: Call<AnalyticsDashboardApiResponse>,
                    response: Response<AnalyticsDashboardApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {

                                numOfVisitorsTV!!.text = visitorLogApiResponse.numOfVisitorsToday

                                setDashboard()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                                numOfVisitorsTV!!.text = "0"
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                            numOfVisitorsTV!!.text = "NA"
                        }
                    }
                }

                override fun onFailure(call: Call<AnalyticsDashboardApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                    numOfVisitorsTV!!.text = "NA"
                }
            })
    }


    private fun setBarChart() {
        val barDataSet = BarDataSet(getData(), "VMS Analytics")
        // barDataSet.barBorderWidth = 0.9f
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        val barData = BarData(barDataSet)
        val xAxis = barChart!!.xAxis
        val yAxis = barChart!!.axisLeft

        val yRight = barChart!!.axisRight
        yRight.setDrawAxisLine(true)
        yRight.setDrawGridLines(false)
        yRight.isEnabled = false
        barChart!!.legend.isEnabled = false

        yAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val months =
            arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
        val formatter = IndexAxisValueFormatter(months)
        xAxis.granularity = 1f
        xAxis.valueFormatter = formatter
        barChart!!.data = barData
        barChart!!.setFitBars(false)
        xAxis.setDrawGridLines(false)
        barChart!!.animateXY(2500, 2500)
        barChart!!.invalidate()

        // Bar Chart Display from ArjCharts ********************

        var labels: ArrayList<String> = ArrayList()
        labels.add("Jan")
        labels.add("Feb")
        labels.add("Mar")
        labels.add("Apr")
        labels.add("May")
        labels.add("Jun")

        val chartDisplayer = ChartDisplayer(
            barChart, labels, barDataSet
        )

        chartDisplayer.displayBarChart()

    }

    private fun getData(): ArrayList<BarEntry> {
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0f, 30f))
        entries.add(BarEntry(1f, 80f))
        entries.add(BarEntry(2f, 60f))
        entries.add(BarEntry(3f, 50f))
        entries.add(BarEntry(4f, 70f))
        entries.add(BarEntry(5f, 60f))
        return entries
    }

    override fun onBackPressed() {
        Log.e("Num", "Fragments: " + supportFragmentManager.backStackEntryCount)
        if (isFragmentLoaded()) {
            // filterBtn!!.visibility = View.VISIBLE
            supportFragmentManager.popBackStack()
        } else {
            // filterBtn!!.visibility = View.GONE
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        Log.e("NUM_OF_FRAG_ONRES: ", "${supportFragmentManager.backStackEntryCount}")
        if (supportFragmentManager.backStackEntryCount == 1) {
            isFromDeptScreen = false
        }

        if (numOfVisTitleTV!!.text == "Today") {
            isFromFilter = false
        }
        Log.e("From filter on resume: ", "$isFromFilter")

//        if (isFragmentLoaded()) {
//            filterBtn!!.visibility = View.VISIBLE
//        } else {
//            filterBtn!!.visibility = View.GONE
//        }
    }

    private fun isFragmentLoaded(): Boolean {
        return (supportFragmentManager.backStackEntryCount > 0)
    }

    private var deptListFromFilter: ArrayList<DeptList> = ArrayList()

    private fun moveToFragment(
        isDepartments: Boolean,
        isFromFilter: Boolean,
        deptList: ArrayList<DeptList>?,
        fromDate: String,
        toDate: String
    ) {
        val fragment =
            DepartmentWiseAnalyticsFragment()

        val arg = Bundle()
        arg.putBoolean("IS_DEPT_VIEW", isDepartments)
        arg.putBoolean("IS_FROM_FILTER", isFromFilter)
        arg.putString("FROM_DATE", fromDate)
        arg.putString("TO_DATE", toDate)
        arg.putParcelableArrayList("DEPT_LIST", deptList)
        fragment.arguments = arg

        val fm = supportFragmentManager
        val fT = fm.beginTransaction()
        fT.replace(R.id.analytics_view, fragment)
        fT.addToBackStack(null)
        fT.commit()
    }

    fun moveToFragment(isDepartments: Boolean, deptCode: String, fromDate: String, toDate: String) {
        val fragment =
            DepartmentWiseAnalyticsFragment()

        val arg = Bundle()
        arg.putBoolean("IS_DEPT_VIEW", isDepartments)
        arg.putString("DEPT_CODE", deptCode)
        arg.putString("FROM_DATE", fromDate)
        arg.putString("TO_DATE", toDate)
        fragment.arguments = arg

        val fm = supportFragmentManager
        val fT = fm.beginTransaction()
        fT.replace(R.id.analytics_view, fragment)
        fT.addToBackStack(null)
        fT.commit()
    }

    private var sheetDialog: BottomSheetDialog? = null
    var fromDateTVFilter: AppCompatTextView? = null
    var toDateTVFilter: AppCompatTextView? = null

    private var fromDateSel: String? = ""
    private var toDateSel: String? = ""

    fun showFilterPopUp(isFromDeptScreen: Boolean) {
        this.isFromFilter = true
        this.isFromDeptScreen = isFromDeptScreen
        sheetDialog = BottomSheetDialog(this)
        val view: View = layoutInflater.inflate(R.layout.vms_analytics_filter_popup, null)

        fromDateTVFilter = view.findViewById(R.id.from_date_tv_vms_filter_analytics)
        toDateTVFilter = view.findViewById(R.id.to_date_tv_vms_filter_analytics)

        val augDatePicker = AugDatePicker(context = this, dateTimeSelectable = this)
        fromDateTVFilter!!.setOnClickListener {
            augDatePicker.showDatePicker(
                isFromDate = true, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = false
            )
        }

        toDateTVFilter!!.setOnClickListener {
            augDatePicker.showDatePicker(
                isFromDate = false, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = false
            )
        }

        // feedbackProgress = view.findViewById<ProgressBar>(R.id.progress_feedback)
        view.findViewById<View>(R.id.filter_apply_btn_sec)
            .setOnClickListener { view1: View? ->
                sheetDialog!!.dismiss()

                if (!fromDateSel.equals(toDateSel)) {
                    numOfVisTitleTV!!.text = "Analytics from ${fromDateSel} to ${toDateSel}"
                } else {
                    numOfVisTitleTV!!.text = "Analytics for ${fromDateSel}"
                }
                if (isFromDeptScreen) {
                    onLoad()
                    getDeptAnalyticsWithFilterApi()
                } else {
//Stay In Same Screen and Set Count

                    onLoad()
                    getDeptAnalyticsWithFilterApi()
                }
            }

        sheetDialog!!.setContentView(view)
        sheetDialog!!.show()
    }

    //DEPT ANALYTICS AND VISITOR COUNT API

    private fun getDeptAnalyticsWithFilterApi() {
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}DepartmentAnalyticsRange"

        apiCallable.loadVDeptAnalyticsFilter(
            url, prefUtil.userName, prefUtil.userName,
            fromDateSel, toDateSel
        )
            .enqueue(object : Callback<DepartmentApiResponse> {
                override fun onResponse(
                    call: Call<DepartmentApiResponse>,
                    response: Response<DepartmentApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {

                                numOfVisitorsTV!!.text =
                                    visitorLogApiResponse.numOfVisitorsCount

                                if (visitorLogApiResponse.deptFilterList != null) {
                                    if (visitorLogApiResponse.deptFilterList!!.isNotEmpty()) {
                                        //setDepartments(visitorLogApiResponse.visitorInDepartments)

                                        deptListFromFilter
                                        deptListFromFilter = visitorLogApiResponse.deptFilterList
                                        afterLoad()

                                        //    numOfVisTitleTV!!.text =

                                        Log.e("FROM_DEPT", ": $isFromDeptScreen")
                                        Log.e("FROM_FILTER", ": $isFromFilter")

                                        val countOfFrag = supportFragmentManager.backStackEntryCount
                                        Log.e("NUM_OF_FRAG_APPLY: ", "$countOfFrag")
                                        if (isFromDeptScreen!! && countOfFrag > 0) {
                                            supportFragmentManager.popBackStack()
                                            moveToFragment(
                                                true,
                                                isFromFilter = isFromFilter!!,
                                                deptList = deptListFromFilter,
                                                fromDate = fromDateSel!!,
                                                toDate = toDateSel!!
                                            )
                                        } else {
                                            showToast(response.body()!!.message!!)
                                        }
                                    } else {
                                        showToast(response.body()!!.message!!)
                                        afterLoad()
                                    }
                                } else {
                                    showToast(response.body()!!.message!!)
                                    afterLoad()
                                }
                            } else {
                                afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<DepartmentApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    override fun onFromDateSelected(date: String) {
        fromDateSel = CalendarUtils.getDateInRequestedFormat("yyyy-MM-dd", "dd-MM-yyyy", date)
        fromDateTVFilter!!.text = fromDateSel
    }

    override fun onToDateSelected(date: String) {
        toDateSel = CalendarUtils.getDateInRequestedFormat("yyyy-MM-dd", "dd-MM-yyyy", date)
        toDateTVFilter!!.text = toDateSel
    }

    override fun onDateSelected(date: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFromTimeSelected(time: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onToTimeSelected(time: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
