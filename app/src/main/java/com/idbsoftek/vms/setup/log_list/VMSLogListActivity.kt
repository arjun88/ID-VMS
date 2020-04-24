package com.idbsoftek.vms.setup.log_list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VMSLogListActivity : VmsMainActivity(),
    VisitorLogItemClickable,
    AdapterView.OnItemSelectedListener, DateTimeSelectable {
    private var visitorLogRV: RecyclerView? = null

    private var isSecurity = false

    private var context: Activity? = null

    private var visitorCategories = ArrayList<String>()
    private var visitorPurposes = ArrayList<String>()
    private var departments = ArrayList<String>()

    private var toMeetForDD = ArrayList<String>()

    private var visitorCategoriesList = ArrayList<VisitorCategoryList>()
    private var visitorPurposesList = ArrayList<VisitorPurposeList>()
    private var departmentsList = ArrayList<DeptList>()

    private var toMeetList = ArrayList<EmpListItem>()
    private var refNumList = ArrayList<VisitorListItem>()
    private var refNumForDD = ArrayList<String>()

    private var deptSel: String = "All"
    private var purposeSel: String = "All"
    private var categorySel: String = "All"

    //FOR SECURITY
    private var refNumSel: String = "All"
    private var toMeetSel: String = "All"

    private var purposeSpinner: AppCompatSpinner? = null
    private var categorySpinner: AppCompatSpinner? = null
    private var deptSpinner: AppCompatSpinner? = null

    private var vmsLogView: View? = null
    private var noDataView: View? = null
    private var noDataTV: AppCompatTextView? = null

    private var loading: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vmslog_list)

        setActionBarTitle("Visitor Log")

        context = this
        initView()
    }

    private fun initView() {
        noDataView = findViewById(R.id.no_data_found_view)
        noDataTV = noDataView!!.findViewById(R.id.no_data_tv_vms_list)
        loading = findViewById(R.id.vms_list_loading)
        isSecurity = intent.getBooleanExtra("IS_SECURITY", false)
        vmsLogView = findViewById(R.id.view_vms_list)

        visitorLogRV = findViewById(R.id.visitor_log_rv)
        visitorLogRV!!.layoutManager = LinearLayoutManager(this@VMSLogListActivity)
        visitorLogRV!!.setHasFixedSize(true)

        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).setOnClickListener {
            showFilterPopUp()
            //showFilterPopUpSecurity()
        }
    }

    override fun onStart() {
        super.onStart()
        if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
            getVisitorLogListApi()
        } else {
            showToast("No Internet!")
        }
    }

    private fun setVisitorLogList(visitorLogList: List<VisitorLogList>) {

        val view: Int? = if (isSecurity) {
            1
        } else {
            0
        }

        val adapter = VistorLogListAdapter(
            this,
            false,
            visitorLogList
        )
        visitorLogRV!!.adapter = adapter
    }

    private fun moveToVisitorLogDetailsScreen(refNum: String, date: String) {
        val intent = Intent(
            this,
            VMSLogListDetailsActivity::class.java
        )
        intent.putExtra("IS_FOR_SECURITY", isSecurity)
        intent.putExtra("REF_NUM", refNum)
        intent.putExtra("VISIT_DATE", date)
        startActivity(intent)
    }

    private var sheetDialog: BottomSheetDialog? = null

    @SuppressLint("DefaultLocale")
    private fun setDeptDD() {
        for (i in 0 until departmentsList.size) {
            val name = departmentsList[i].name.toLowerCase().capitalize()

            departments.add(name)
        }
        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, departments
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        deptSpinner!!.adapter = adapter
        deptSpinner!!.onItemSelectedListener = this
    }

    //SECURITY ***********************

    @SuppressLint("DefaultLocale")
    private fun setRefNumDD() {
        for (i in 0 until refNumList.size) {
            val name = refNumList[i].visitorRefNum!!

            refNumForDD.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, refNumForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        refNumSpinner!!.adapter = adapter
        refNumSpinner!!.onItemSelectedListener = this
    }

    @SuppressLint("DefaultLocale")
    private fun setToMeetDD() {
        for (i in 0 until toMeetList.size) {
            val name = toMeetList[i].name!!//.toLowerCase().capitalize()
            toMeetForDD.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, toMeetForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        toMeetSpinner!!.adapter = adapter
        toMeetSpinner!!.onItemSelectedListener = this
    }

    // *******************************

    private fun onNoData() {
        noDataView!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE
        visitorLogRV!!.visibility = View.GONE
        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility = View.GONE
    }

    private fun onLoad() {
        noDataView!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
        visitorLogRV!!.visibility = View.GONE
        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility = View.GONE
    }

    private fun afterLoad() {
        noDataView!!.visibility = View.GONE
        loading!!.visibility = View.GONE
        visitorLogRV!!.visibility = View.VISIBLE

        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility = View.VISIBLE
        //   visitorLogRV!!.background.alpha = 70
//        visitorLogRV!!.background.alpha = 100
    }

    private fun afterActionLoad() {
        noDataView!!.visibility = View.GONE
        loading!!.visibility = View.GONE
        visitorLogRV!!.visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility = View.VISIBLE

        vmsLogView!!.background.alpha = 0
    }

    private fun onAction() {
        noDataView!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
        visitorLogRV!!.visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility = View.GONE
        //  vmsLogView!!.background.alpha = 100
    }

    @SuppressLint("DefaultLocale")
    private fun setCategoryDD() {
        for (i in 0 until visitorCategoriesList.size) {
            val name = visitorCategoriesList[i].name.toLowerCase().capitalize()

            visitorCategories.add(name)
        }
        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, visitorCategories
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        categorySpinner!!.adapter = adapter
        categorySpinner!!.onItemSelectedListener = this
    }

    @SuppressLint("DefaultLocale")
    private fun setPurposeDD() {
        for (i in 0 until visitorPurposesList.size) {
            val name = visitorPurposesList[i].name.toLowerCase().capitalize()

            visitorPurposes.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, visitorPurposes
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        purposeSpinner!!.adapter = adapter
        purposeSpinner!!.onItemSelectedListener = this
    }

    //FILTER POP-UP MGR
    private var fromDateSel: String = ""
    private var toDateSel: String = ""

    private var fromDateTV: AppCompatTextView? = null
    private var toDateTV: AppCompatTextView? = null
    private var toMeetSpinner: AppCompatSpinner? = null
    private var refNumSpinner: AppCompatSpinner? = null

    private var deptFilterView: LinearLayoutCompat? = null

    private fun showFilterPopUp() {
        sheetDialog = BottomSheetDialog(this@VMSLogListActivity)
        val view: View = layoutInflater.inflate(R.layout.visitor_log_filter_popup, null)

        deptFilterView = view.findViewById(R.id.dept_view_vms_filter)

        purposeSpinner = view.findViewById(R.id.purpose_vms_log_spinner)
        categorySpinner = view.findViewById(R.id.category_vms_log_spinner)
        deptSpinner = view.findViewById(R.id.dept_vms_log_spinner)

        fromDateTV = view.findViewById(R.id.from_date_tv_vms_filter_mgr)
        toDateTV = view.findViewById(R.id.to_date_tv_vms_filter_mgr)

        val augDatePicker = AugDatePicker(this@VMSLogListActivity, this)
        fromDateTV!!.setOnClickListener {
            augDatePicker.showDatePicker(
                isFromDate = true, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = false
            )
        }

        toDateTV!!.setOnClickListener {
            augDatePicker.showDatePicker(
                isFromDate = false, isSingleDate = false, fromDate = "", toDate = "",
                futureDateCanbeSelected = true
            )
        }

        if (PrefUtil.getVmsEmpROle() == "approver") {
            deptFilterView!!.visibility = View.GONE
        } else {
            deptFilterView!!.visibility = View.VISIBLE
        }

        if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
            if (PrefUtil.getVmsEmpROle() != "approver") {
                getDeptApi()
            }
            getVisitorCategoryApi()
            getPurposeApi()
        } else {
            showToast("No Internet!")
        }

        view.findViewById<View>(R.id.filter_apply_btn)
            .setOnClickListener {

                if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
                    if (fromDateSel.isEmpty()) {
                        showToast("From Date Can't be empty!")
                    } else if (toDateSel.isEmpty()) {
                        showToast("To Date Can't be empty!")
                    }
                    else if (CalendarUtils.isFirstDateLesserThanSecondDate(
                            fromDateSel,
                            toDateSel, "dd-MM-yyyy"
                        )
                    ) {
                        sheetDialog!!.dismiss()
                        applyFilterApiMgr()

                    } else
                        showToast("From Date can't be greater than To Date")

                } else {
                    showToast("No Internet!")
                }
            }

        sheetDialog!!.setContentView(view)
        sheetDialog!!.show()
    }

    override fun onVisitorLogItemClick(id: String, date: String) {
        moveToVisitorLogDetailsScreen(id, date)
    }

    override fun onVisitorLogAction(id: String, action: String) {
        if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
            if (PrefUtil.getVmsEmpROle() == "security") {
                //makeAction(action, id)
                showGatePickerPopUp(action, id)
            } else {
                gateSel = ""
                makeAction(action, id)
            }
        } else {
            showToast("No Internet!")
        }
    }

    override fun onVisitorImgClick(image: String) {
        showImagePopUp(this@VMSLogListActivity, image)
    }

    private fun applyFilterApiSec() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}SecurityFilter"

        apiCallable.aplyFilterSec(
            url, prefUtil.userName, prefUtil.sessionID,
            refNumSel,
            toMeetSel,
            categorySel,
            fromDateSel,
            toDateSel
        )
            .enqueue(object : Callback<VisitorLogApiResponse> {
                override fun onResponse(
                    call: Call<VisitorLogApiResponse>,
                    response: Response<VisitorLogApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                setVisitorLogList(visitorLogApiResponse.filterListFromApprover!!)
                                afterLoad()
                            } else {
                                // afterLoad()
                                setVisitorLogList(visitorLogApiResponse.filterListFromApprover!!)
                                onNoData()

                                //  showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorLogApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun applyFilterApiMgr() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}ApproverFilter"

        apiCallable.aplyFilterMgr(
            url, prefUtil.userName, prefUtil.sessionID,
            deptSel,
            purposeSel,
            categorySel,
            fromDateSel,
            toDateSel
        )
            .enqueue(object : Callback<VisitorLogApiResponse> {
                override fun onResponse(
                    call: Call<VisitorLogApiResponse>,
                    response: Response<VisitorLogApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                setVisitorLogList(visitorLogApiResponse.filterListFromApprover!!)
                                afterLoad()
                            } else {
                                afterLoad()
                                if (visitorLogApiResponse.filterListFromApprover == null) {
                                    visitorLogApiResponse.filterListFromApprover = ArrayList()
                                } else
                                    setVisitorLogList(visitorLogApiResponse.filterListFromApprover!!)
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            // afterLoad()
                            onNoData()
                            noDataTV!!.text = "Server Error"
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorLogApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    onNoData()
                    noDataTV!!.text = "Couldn't reach server"
                }
            })
    }

    //API
    private fun getVisitorCategoryApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorCategory"

        apiCallable.getVisitorCategories(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<VisitorCategoryApiResponse> {
                override fun onResponse(
                    call: Call<VisitorCategoryApiResponse>,
                    response: Response<VisitorCategoryApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                visitorCategoriesList.clear()
                                visitorCategories.clear()

                                var dept = VisitorCategoryList()
                                dept.code = "All"
                                dept.name = "All"

                                visitorCategoriesList.add(dept)

                                for (i in 0 until visitorLogApiResponse.visitorCategoryList.size) {
                                    visitorCategoriesList.add(visitorLogApiResponse.visitorCategoryList[i])
                                }

                                setCategoryDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorCategoryApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun getPurposeApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorPurpose"

        apiCallable.getVisitorPurpose(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<VisitorPurposeApiResponse> {
                override fun onResponse(
                    call: Call<VisitorPurposeApiResponse>,
                    response: Response<VisitorPurposeApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                visitorPurposesList.clear()
                                visitorPurposes.clear()

                                var dept = VisitorPurposeList()
                                dept.code = "All"
                                dept.name = "All"

                                visitorPurposesList.add(dept)

                                for (i in 0 until visitorLogApiResponse.visitorPurposeList.size) {
                                    visitorPurposesList.add(visitorLogApiResponse.visitorPurposeList[i])
                                }

                                setPurposeDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorPurposeApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    //FOR SECURITY ********************

    private fun getRefNumApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorSecurityList"

        apiCallable.getRefNumList(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<RefNumListApiResponse> {
                override fun onResponse(
                    call: Call<RefNumListApiResponse>,
                    response: Response<RefNumListApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                refNumList.clear()
                                refNumForDD.clear()

                                var dept = VisitorListItem()
                                dept.visitorRefNum = "All"
                                dept.name = "All"

                                refNumList.add(dept)

                                for (element in visitorLogApiResponse.visitorList!!) {
                                    refNumList.add(element!!)
                                }

                                setRefNumDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<RefNumListApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    private fun getToMeetApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}EmployeeList"

        apiCallable.getToMeetList(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<ToMeetApiResponse> {
                override fun onResponse(
                    call: Call<ToMeetApiResponse>,
                    response: Response<ToMeetApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                toMeetList.clear()
                                toMeetForDD.clear()

                                var dept = EmpListItem()
                                dept.code = "All"
                                dept.name = "All"

                                toMeetList.add(dept)

                                for (element in visitorLogApiResponse.empList!!) {
                                    toMeetList.add(element!!)
                                }

                                setToMeetDD()

                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<ToMeetApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }


    // ****************************************


    private fun getDeptApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}Department"

        apiCallable.getDepartments(
            url, prefUtil.userName, prefUtil.userName
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
                                departmentsList.clear()
                                departments.clear()

                                var dept = DeptList()
                                dept.code = "All"
                                dept.name = "All"

                                departmentsList.add(dept)

                                for (i in 0 until visitorLogApiResponse.deptList.size) {
                                    departmentsList.add(visitorLogApiResponse.deptList[i])
                                }

                                setDeptDD()


                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<DepartmentApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    // ACTIONS API **************

    private fun makeAction(action: String, refNum: String) {
        onAction()
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorRequest"

        apiCallable.doVisitorAction(
            url, prefUtil.userName,
            prefUtil.userName, refNum,
            action
        )
            .enqueue(object : Callback<VisitorActionApiResponse> {
                override fun onResponse(
                    call: Call<VisitorActionApiResponse>,
                    response: Response<VisitorActionApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {

                                showToast(response.body()!!.message!!)
                                afterActionLoad()
                                getVisitorLogListApi()

                                //afterLoad()
                            } else {
                                //afterLoad()
                                afterActionLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorActionApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                    afterActionLoad()
                }
            })
    }

    // **** GATE CONCEPT *********

    private var gateSel: String? = null
    private var gateSpinner: AppCompatSpinner? = null

    private fun getGatesApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorGate"

        apiCallable.getGates(
            url, prefUtil.userName, prefUtil.userName
        )
            .enqueue(object : Callback<GateListingApiResponse> {
                override fun onResponse(
                    call: Call<GateListingApiResponse>,
                    response: Response<GateListingApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                gatesList.clear()
                                gates.clear()

                                for (element in visitorLogApiResponse.gatesListing!!) {
                                    gatesList.add(element!!)
                                }

                                setGatesDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<GateListingApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    // GATE POP-UP

    private var gateSheetDialog: BottomSheetDialog? = null

    private fun showGatePickerPopUp(action: String, id: String) {

        gateSheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.gate_popup, null)
        gateSpinner = view.findViewById(R.id.gate_spinner)

        getGatesApi()

        view.findViewById<View>(R.id.gate_submit_btn)
            .setOnClickListener {
                gateSheetDialog!!.dismiss()
                if (AppUtil.isInternetThere(context!!)) {
                    makeAction(action, id)
                    //

                } else {
                    showToast("No Internet!")
                }
            }

        gateSheetDialog!!.setContentView(view)
        gateSheetDialog!!.show()
    }

    @SuppressLint("DefaultLocale")
    private fun setGatesDD() {
        var defGatePos = 0
        for (i in 0 until gatesList.size) {
            if (gatesList[i].isDefault == true) {
                defGatePos = i
            }
            val name = gatesList[i].name!!.toLowerCase().capitalize()

            gates.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, gates
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        gateSpinner!!.adapter = adapter
        gateSpinner!!.setSelection(defGatePos)
        gateSpinner!!.onItemSelectedListener = this
    }

    private var gatesList: ArrayList<GatesListingItem> = ArrayList()

    private var gates: ArrayList<String> = ArrayList()

    // ************************

// **********    GET All Visitor Log List API

    private fun getVisitorLogListApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val empID = prefUtil.userName //prefUtil.userName

        val url = "${prefUtil.appBaseUrl}VisitorLogList"

        apiCallable.getVisitorLogList(
            url, empID, empID //AppUtil.EMP_ID_VMS, AppUtil.EMP_ID_VMS
        )
            .enqueue(object : Callback<VisitorLogApiResponse> {
                override fun onResponse(
                    call: Call<VisitorLogApiResponse>,
                    response: Response<VisitorLogApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                setVisitorLogList(visitorLogApiResponse.visitorLogList!!)
                                afterLoad()
                            } else {
                                //afterLoad()
                                onNoData()
                                findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility =
                                    View.VISIBLE
                                //showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            onNoData()
                            noDataTV!!.text = "Server Error"
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorLogApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    onNoData()
                    noDataTV!!.text = "Couldn't reach server"
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0) {
            deptSpinner -> {
                deptSel = departmentsList[p2].code
            }
            categorySpinner -> {
                categorySel = visitorCategoriesList[p2].code
            }

            purposeSpinner -> {
                purposeSel = visitorPurposesList[p2].code
            }

            refNumSpinner -> {
                refNumSel = refNumList[p2].visitorRefNum!!
            }

            toMeetSpinner -> {
                toMeetSel = toMeetList[p2].code!!
            }

            gateSpinner -> {
                gateSel = gatesList[p2].code!!
            }
        }
    }

    override fun onFromDateSelected(date: String) {
        var dateSel = date
        dateSel = CalendarUtils.getDateInRequestedFormat(
            "yyyy-MM-dd",
            "dd-MM-yyyy", date
        )

        fromDateSel = dateSel
        fromDateTV!!.text = dateSel
    }

    override fun onToDateSelected(date: String) {
        var dateSel = date
        dateSel = CalendarUtils.getDateInRequestedFormat(
            "yyyy-MM-dd",
            "dd-MM-yyyy", date
        )

        toDateSel = dateSel
        toDateTV!!.text = dateSel
    }

    override fun onDateSelected(date: String) {
    }

    override fun onFromTimeSelected(time: String) {
    }

    override fun onToTimeSelected(time: String) {
    }

    //IMAGE POP-UP

    private fun showImagePopUp(context: Context, image: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.image_pop_up)

        val visitorIV = dialog.findViewById<View>(R.id.image_in_pop_up) as AppCompatImageView

        Glide
            .with(context)
            .load(image)
            .placeholder(R.drawable.account)
            .apply(RequestOptions.placeholderOf(R.drawable.account).error(R.drawable.account))
            .into(visitorIV)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog.window!!.attributes = lp
    }

}
