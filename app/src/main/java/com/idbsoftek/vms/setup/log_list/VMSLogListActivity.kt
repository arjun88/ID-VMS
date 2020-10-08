package com.idbsoftek.vms.setup.log_list

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.ScanQrActivity
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class VMSLogListActivity : VmsMainActivity(),
    VisitorLogItemClickable,
    AdapterView.OnItemSelectedListener, DateTimeSelectable, TokenRefreshable {
    private var visitorLogRV: RecyclerView? = null

    private var isSecurity = false
    private val CAMERA_ACCESS_REQ_CODE = 88
    private val CAMERA_RETURN_CODE = 888

    private var context: Activity? = null

    private var visitorCategories = ArrayList<String>()
    private var statusListDD = ArrayList<String>()
    private var departments = ArrayList<String>()

    private var toMeetForDD = ArrayList<String>()

    private var visitorCategoriesList = ArrayList<VisitorCategoryList>()
    private var visitStatusList = ArrayList<VMSUtil.Companion.StatusUtil>()
    private var departmentsList = ArrayList<DeptList>()

    private var toMeetList = ArrayList<EmpListItem>()
    private var refNumList = ArrayList<VisitorListItem>()
    private var refNumForDD = ArrayList<String>()

    private var deptSel: String = "All"
    private var statusSel: String = "All"
    private var categorySel: String = "All"

    //FOR SECURITY
    private var refNumSel: String = "All"
    private var toMeetSel: String = "All"

    private var statuSpinner: AppCompatSpinner? = null
    private var categorySpinner: AppCompatSpinner? = null
    private var deptSpinner: AppCompatSpinner? = null

    private var vmsLogView: View? = null
    private var noDataView: View? = null
    private var noDataTV: AppCompatTextView? = null

    private var loading: ProgressBar? = null
    private var disposable: CompositeDisposable? = null
    private var tokenRefresh: TokenRefresh? = null
    private var searchView: SearchView? = null
    private var tokenRefreshSel: TokenRefreshable? = null

    private val permissionsReq = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vmslog_list)

        setActionBarTitle("Visitor Log")

        context = this

        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)
        disposable = CompositeDisposable()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable = null
    }

    private fun doFiltering(searchText: String?) {
        visitorLogListFiltered.clear()
        for (visitor in logList) {
            if (visitor.visitorName!!.toLowerCase(Locale.ROOT).contains(searchText!!) || visitor.requestID.toString()
                    .contains(
                        searchText
                    )
                || visitor.visitorMobile!!.contains(searchText)
            ) {
                visitorLogListFiltered.add(visitor)
            }
        }

        setVisitorLogList(visitorLogListFiltered)
    }

    private fun searchImplementation() {
        searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("Search", "Submit")
                //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty())
                    doFiltering(newText.toString().toLowerCase(Locale.ROOT))
                else
                    setVisitorLogList(logList)

                return true
            }

        })

    }

    private fun checkForCameraPermission(): Boolean {
        for (permission in permissionsReq) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun reqCameraAccess() {
        ActivityCompat.requestPermissions(
            context!!,
            permissionsReq, CAMERA_ACCESS_REQ_CODE
        )
    }

    private fun setUpCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkForCameraPermission()) {
                reqCameraAccess()
            } else
            //  dispatchTakePictureIntent()
                moveToScan()
        } else
        // dispatchTakePictureIntent()
            moveToScan()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            -1 -> {

            }
            CAMERA_ACCESS_REQ_CODE -> {
                if (grantResults.isNotEmpty()) {

                    if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    )
                        moveToScan()
                    else
                        reqCameraAccess()
                } else {
                    reqCameraAccess()
                }
            }
        }
    }


    private fun initView() {
        searchView = findViewById(R.id.log_search_view)
        searchImplementation()

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

        findViewById<MaterialButton>(R.id.scan_qr_btn_vms_list).setOnClickListener {
          setUpCamera()
        }
    }

    private fun moveToScan(){
        val intent = Intent(this, ScanQrActivity::class.java)
        //startActivity(intent)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

      //  if(resultCode == RESULT_OK) {
            // check if the request code is same as what is passed  here it is 2
            if (resultCode == 100) {
                val passID = data!!.getStringExtra("PASS_ID")

                //MOVE TO Details Screen
                moveToVisitorLogDetailsScreen(passID!!, "22-09-2020")
            }
       /* }
        else if(requestCode == RESULT_CANCELED){

        }*/
    }

    override fun onStart() {
        super.onStart()
        if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
            getVisitorLogListApiRx()
        } else {
            showToast("No Internet!")
        }
    }

    private var visitorLogListFiltered: ArrayList<VisitorListItem> = ArrayList()
    var logList: ArrayList<VisitorListItem> = ArrayList()
    private fun applyFilterUsingStatus() {
        // setVisitorLogList(visitorLogListFiltered)
        if (logList.size > 0) {
            visitorLogListFiltered.clear()
            for (log in logList) {
                if (log.status == statusSel.toInt())
                    visitorLogListFiltered.add(log)
            }

            if (visitorLogListFiltered.size > 0) {
                showToast("Showing ${visitorLogListFiltered.size} Records")
                setVisitorLogList(visitorLogListFiltered)
            } else {
                setVisitorLogList(visitorLogListFiltered)
                showToast("Record not found on filtered status")
            }
            sheetDialog!!.dismiss()
        }
    }

    private fun setVisitorLogList(visitorLogList: List<VisitorListItem>) {

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
       // visitorLogRV!!.itemAnimator = visitorLogRV!!.itemAnimator as SimpleItemAnimator

        (visitorLogRV!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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
        for (i in 0 until visitStatusList.size) {
            val name = visitStatusList[i].name!!.toLowerCase().capitalize()

            statusListDD.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, statusListDD
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        statuSpinner!!.adapter = adapter
        statuSpinner!!.onItemSelectedListener = this
    }

    @SuppressLint("DefaultLocale")
    private fun setStatusDD() {
        visitStatusList.clear()
        statusListDD.clear()
        visitStatusList.addAll(VMSUtil.getStatusList())
        for (element in visitStatusList) {
            val name = element.name!!.toLowerCase().capitalize()

            statusListDD.add(name)
        }

        val adapter = ArrayAdapter(
            this@VMSLogListActivity,
            android.R.layout.simple_spinner_item, statusListDD
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        statuSpinner!!.adapter = adapter
        statuSpinner!!.onItemSelectedListener = this
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

        statuSpinner = view.findViewById(R.id.status_vms_log_spinner)
        categorySpinner = view.findViewById(R.id.category_vms_log_spinner)
        deptSpinner = view.findViewById(R.id.dept_vms_log_spinner)

        fromDateTV = view.findViewById(R.id.from_date_tv_vms_filter_mgr)
        toDateTV = view.findViewById(R.id.to_date_tv_vms_filter_mgr)

        val augDatePicker = AugDatePicker(this@VMSLogListActivity, this)
        fromDateTV!!.setOnClickListener {
            augDatePicker.showDatePickerForFilter(
                isFromDate = true, isSingleDate = false, fromDate = "", toDate = ""
            )
        }

        toDateTV!!.setOnClickListener {
            augDatePicker.showDatePickerForFilter(
                isFromDate = false, isSingleDate = false, fromDate = "", toDate = ""
            )
        }

        setStatusDD()

        /*if (PrefUtil.getVmsEmpROle() == "approver") {
            deptFilterView!!.visibility = View.GONE
        } else {
            deptFilterView!!.visibility = View.VISIBLE
        }*/

        /* if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
             if (PrefUtil.getVmsEmpROle() != "approver") {
                 getDeptApi()
             }
             getVisitorCategoryApi()
             getPurposeApi()
         } else {
             showToast("No Internet!")
         }*/

        view.findViewById<View>(R.id.filter_apply_btn)
            .setOnClickListener {

                if (AppUtil.isInternetThere(this@VMSLogListActivity)) {
                    applyFilterUsingStatus()
                    /*if (fromDateSel.isEmpty()) {
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
                        showToast("From Date can't be greater than To Date")*/

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

    //API
    //FOR SECURITY ********************


    // ****************************************


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
                                getVisitorLogListApiRx()

                                //afterLoad()
                            } else {
                                //afterLoad()
                                afterActionLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {

                        }
                        response.code() == 500 -> {
                            //  afterLoad()
                            afterActionLoad()
                            showToast("Server Error!")
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

    private fun getVisitorLogListApiRx() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val empID = prefUtil.userName //prefUtil.userName

        val url =
            "https://vms.idbssoftware.com/api/VisitorListing/VMCList" //"${prefUtil.appBaseUrl}VisitorLogList"

        disposable!!.add(apiCallable.getVisitorLogApi(
            url,
            prefUtil.getApiToken()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeOn(Schedulers.io())
            .subscribe(
                { apiRes -> onVisitorListFetchSuccess(apiRes) },

                { error -> onVisitorListFetchFailed(error.message!!, error = error) }
            ))
    }

    private fun onVisitorListFetchSuccess(visitorLogApiResponse: VisitorLogListApiResponse) {
        if (visitorLogApiResponse.status == true) {
            visitorLogListFiltered.clear()
            logList.clear()
            logList.addAll(visitorLogApiResponse.visitorList!!)
            visitorLogListFiltered.addAll(visitorLogApiResponse.visitorList)
            setVisitorLogList(visitorLogApiResponse.visitorList)
            afterLoad()
        } else {
            onNoData()
            findViewById<FloatingActionButton>(R.id.filter_btn_vms_list).visibility =
                View.VISIBLE
            //showToast(response.body()!!.message!!)
        }
    }

    private fun onVisitorListFetchFailed(msg: String, error: Throwable) {
        val httpExc: HttpException = error as HttpException
        httpExc.code()
        onNoData()
        noDataTV!!.text = msg
        when(httpExc.code()){
            401 -> {
                this.tokenRefreshSel = this
                tokenRefresh!!.doTokenRefresh(context!!, this.tokenRefreshSel)
            }
        }
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

            statuSpinner -> {
                statusSel = visitStatusList[p2].code.toString()
            }

            /*  refNumSpinner -> {
                  refNumSel = refNumList[p2].visitorRefNum!!
              }*/

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

    override fun onTokenRefresh(responseCode: Int, token: String) {
        afterLoad()
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(context!!)
            }
            200 -> {
                getVisitorLogListApiRx()
            }
            else -> {
                AppUtil.onSessionOut(context!!)
            }
        }
    }

}
