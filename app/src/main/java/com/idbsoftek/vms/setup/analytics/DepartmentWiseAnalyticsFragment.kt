package com.idbsoftek.vms.setup.analytics

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VisitorActionApiResponse
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.setup.log_list.VisitorListItem
import com.idbsoftek.vms.setup.log_list.VisitorLogItemClickable
import com.idbsoftek.vms.setup.log_list.VistorLogListAdapter
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.setup.visitor_stats.DashboardListItem
import com.idbsoftek.vms.setup.visitor_stats.DbVisitorStatsApiResponse
import com.idbsoftek.vms.setup.visitor_stats.StatsOfVisitorApiResponse
import com.idbsoftek.vms.setup.visitor_stats.VisitorStatsActivity
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.AugDatePicker
import com.idbsoftek.vms.util.DateTimeSelectable
import com.idbsoftek.vms.util.PrefUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentWiseAnalyticsFragment : Fragment(),
    DeptItemClickable,
    VisitorLogItemClickable, TokenRefreshable, AdminActionable, AdapterView.OnItemSelectedListener,
    DateTimeSelectable {
    private var activity: AppCompatActivity? = null
    private var analyticsActivity: VmsAnalyticsActivity? = null
    private var statsActivity: VisitorStatsActivity? = null
    private var deptRV: RecyclerView? = null
    private var viewa: View? = null
    private var isDeptView: Boolean = true
    private var loading: ProgressBar? = null
    private var deptCodeSel: String = ""
    private var deptList: List<DashboardListItem>? = ArrayList()

    private var fromDate: String? = ""
    private var toDate: String? = ""

    private var isFromFilter: Boolean? = false

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    private var fromStats: Boolean? = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewa = inflater.inflate(R.layout.fragment_department_wise_analytics, container, false)

        val arg = arguments

        augDatePicker = AugDatePicker(context!!, this)

        isDeptView = arg!!.getBoolean("IS_DEPT_VIEW")
        deptCodeSel = arg.getString("DEPT_CODE", "")
        fromDate = arg.getString("FROM_DATE", "")
        toDate = arg.getString("TO_DATE", "")
        isFromFilter = arg.getBoolean("IS_FROM_FILTER", false)
        deptList = arg.getParcelableArrayList("DEPT_LIST")

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        initView()
        return viewa
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val arg = arguments

        fromStats = arg!!.getBoolean("FROM_STATS")
        activity = getActivity() as AppCompatActivity?
        if (fromStats == true) {
            statsActivity = activity as VisitorStatsActivity
            statsActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            analyticsActivity = activity as VmsAnalyticsActivity
            analyticsActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        titleAndViewSetUp()
    }

    override fun onResume() {
        super.onResume()
        titleAndViewSetUp()
    }

    private fun titleAndViewSetUp() {
        if (fromStats!!) {
            if (isDeptView) {
                activity!!.supportActionBar!!.title = "Departments Analytics"
                // statsActivity!!.filterBtn!!.visibility = View.VISIBLE

            } else {
                activity!!.supportActionBar!!.title = "Visitors In Departments"
                // analyticsActivity!!.filterBtn!!.visibility = View.GONE
            }
        } else {
            if (isDeptView) {
                activity!!.supportActionBar!!.title = "Departments Analytics"
                analyticsActivity!!.filterBtn!!.visibility = View.VISIBLE

            } else {
                activity!!.supportActionBar!!.title = "Visitors In Departments"
                analyticsActivity!!.filterBtn!!.visibility = View.GONE
            }
        }


    }

    private fun initView() {
        loading = viewa!!.findViewById(R.id.dept_progress)
        deptRV = viewa!!.findViewById(R.id.departments_rv)
        deptRV!!.layoutManager = LinearLayoutManager(activity!!)
        deptRV!!.setHasFixedSize(true)

        if (!fromStats!!) {
            analyticsActivity!!.filterBtn!!.setOnClickListener {
                Log.e("Filter", "Pop Up")
                analyticsActivity!!.showFilterPopUp(true)
            }
        }

        if (isDeptView) {
            //setDepartments()
            if (isFromFilter!!) {
                // setDepartments(deptList = deptList!!)
                getDeptAnalyticsApi()
            } else
                getDeptAnalyticsApi()
        } else {
//            val visitorLogList = ArrayList<VisitorLogList>()
//            setVisitorsInDept(visitorLogList)

            getVisitorLogListApi()
        }
    }

    private fun setDepartments(deptList: List<DashboardListItem>?) {
        val adapter =
            DeptListAdapter(
                this,
                deptList!!
            )
        deptRV!!.adapter = adapter
    }

    private fun setVisitorsInDept(visitorsList: List<VisitorListItem>) {
        val adapter = VistorLogListAdapter(
            this,
            true,
            visitorLogList = visitorsList, this
        )
        deptRV!!.adapter = adapter
    }

    private fun onLoad() {
        deptRV!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        deptRV!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        if (activity != null)
            if (isDeptView)
                activity!!.supportActionBar!!.title = "Visitor Analytics"
            else {
                activity!!.supportActionBar!!.title = "Departments"
            }
    }

    override fun onDeptClick(id: String) {
        deptCodeSel = id
        // analyticsActivity!!.moveToFragment(false)
        analyticsActivity!!.moveToFragment(
            false, deptCodeSel, fromDate = fromDate!!,
            toDate = toDate!!
        )
    }

    override fun onVisitorLogItemClick(id: String, date: String) {

    }

    override fun onVisitorLogAction(id: String, action: String) {

    }

    override fun onVisitorImgClick(image: String) {
        showImagePopUp(activity!!, image)
    }

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

    private fun getVisitorLogListApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${PrefUtil.getBaseUrl()}Stats/VisitorListRangeApi"
        tokenRefreshSel = this

        apiCallable.loadVisitorsInDeptAnalytics(
            url, prefUtil.getApiToken(),
            fromDate, toDate,
            deptCodeSel
        ).enqueue(object : Callback<StatsOfVisitorApiResponse> {
            override fun onResponse(
                call: Call<StatsOfVisitorApiResponse>,
                response: Response<StatsOfVisitorApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        val visitorLogApiResponse = response.body()
                        if (visitorLogApiResponse!!.status == true) {
                            setVisitorsInDept(visitorLogApiResponse.visitorListCount!!)
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

            override fun onFailure(call: Call<StatsOfVisitorApiResponse>, t: Throwable) {
                t.printStackTrace()
                afterLoad()
            }
        })
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
                                    setDepartments(visitorLogApiResponse.dashboardList)
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

    override fun onTokenRefresh(responseCode: Int, token: String) {
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(activity!!)
            }
            200 -> {
                if (isDeptView) {
                    //setDepartments()
                    if (isFromFilter!!) {
                        setDepartments(deptList = deptList!!)
                    } else
                        getDeptAnalyticsApi()
                } else {
                    getVisitorLogListApi()
                }
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
    }

    private var rejectSheetDialog: BottomSheetDialog? = null
    private var comment = ""

    var dateTV: AppCompatTextView? = null
    var timeTV: AppCompatTextView? = null
    var gateTV: AppCompatTextView? = null

    var gateSpinner: AppCompatSpinner? = null
    var statusSpinner: AppCompatSpinner? = null

    var visitStatusList: ArrayList<VMSUtil.Companion.StatusUtil> = ArrayList()

    var statusListDD: ArrayList<String> = ArrayList()

    @SuppressLint("DefaultLocale")
    private fun setStatusDD() {
        statusListDD.clear()

        for (element in visitStatusList) {
            val name = element.name!!.toLowerCase().capitalize()

            statusListDD.add(name)
        }

        val adapter = ArrayAdapter(
            activity!!,
            android.R.layout.simple_spinner_item, statusListDD
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        statusSpinner!!.adapter = adapter
        statusSpinner!!.onItemSelectedListener = this
    }

    private var augDatePicker: AugDatePicker? = null

    private var rectBg: RelativeLayout? = null

    private fun showActionPopUp(visitor: VisitorListItem) {

        rejectSheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.admin_action_popup, null)
        val commentTxtIP: TextInputLayout = view.findViewById(R.id.admin_comment_txt_ip)
        val submitBtn: MaterialButton = view.findViewById(R.id.submit_admin_btn)
        dateTV = view.findViewById(R.id.date_tv_admin_action_vms)
        timeTV = view.findViewById(R.id.time_admin_vms_form)

        gateTV = view.findViewById(R.id.gate_title_tv)
        rectBg = view.findViewById(R.id.gate_spinner_bg)

        statusSpinner = view.findViewById(R.id.status_spinner_admin)
        gateSpinner = view.findViewById(R.id.gate_spinner_admin)


        if (visitor.status == VMSUtil.MeetStartAction) {
            statusListDD.clear()
            visitStatusList.clear()
            var statusUtil = VMSUtil.Companion.StatusUtil(4, "Meet Completed")
            visitStatusList.add(statusUtil)

            statusUtil = VMSUtil.Companion.StatusUtil(VMSUtil.CheckOutAction, "Exit")
            visitStatusList.add(statusUtil)

            setStatusDD()
        } else if (visitor.status == VMSUtil.MeetCompleteAction) {
            statusListDD.clear()
            visitStatusList.clear()

            var statusUtil = VMSUtil.Companion.StatusUtil(VMSUtil.CheckOutAction, "Exit")
            visitStatusList.add(statusUtil)

            setStatusDD()
        } else {
            statusListDD.clear()
            visitStatusList.clear()

            var statusUtil = VMSUtil.Companion.StatusUtil(3, "Meet Start")
            visitStatusList.add(statusUtil)

            statusUtil = VMSUtil.Companion.StatusUtil(4, "Meet Completed")
            visitStatusList.add(statusUtil)

            statusUtil = VMSUtil.Companion.StatusUtil(VMSUtil.CheckOutAction, "Exit")
            visitStatusList.add(statusUtil)

            setStatusDD()
        }

        dateTV!!.setOnClickListener {
            augDatePicker!!.showDatePicker(
                isFromDate = false, isSingleDate = true, fromDate = "", toDate = "",
                futureDateCanbeSelected = false
            )
        }

        timeTV!!.setOnClickListener {
            augDatePicker!!.showTimePicker(
                isFromTime = true, inTime = "", outTime = ""
            )
        }

        getGatesApi()

        submitBtn.setOnClickListener {
            comment = commentTxtIP.editText!!.text.toString()
            if (AppUtil.isInternetThere(context!!)) {
                when {
                    dateSel.isEmpty() -> showToast("Please Select Date")
                    timeSel.isEmpty() -> {
                        showToast("Please Select Time")
                    }
                    else -> {
                        rejectSheetDialog!!.dismiss()
                        makeAction(visitor)
                    }
                }
                //
            } else {
                showToast("No Internet!")
            }
        }

        rejectSheetDialog!!.setContentView(view)
        rejectSheetDialog!!.show()
    }

    override fun onAdminAction(visitor: VisitorListItem) {
       // showActionPopUp(visitor)
    }

    private var statusSel = 0
    private var gateSel = ""

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == statusSpinner) {
            statusSel = visitStatusList[position].code!!
            if (statusSel == 3 || statusSel == 4) {
                gateTV!!.visibility = View.GONE
                gateSpinner!!.visibility = View.GONE
                rectBg!!.visibility = View.GONE
            } else {
                gateTV!!.visibility = View.VISIBLE
                gateSpinner!!.visibility = View.VISIBLE
                rectBg!!.visibility = View.VISIBLE
            }
        } else if (parent == gateSpinner) {
            gateSel = gatesList[position].code!!
        }
    }

    private var gatesList: ArrayList<GatesListingItem> = ArrayList()

    private var gates: ArrayList<String> = ArrayList()

    private fun getGatesApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${prefUtil.appBaseUrl}VisitorListing/GetAllGatesInfo"
        tokenRefreshSel = this
        apiCallable.getGatesApi(
            url, prefUtil.getApiToken()
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
                                    gatesList.add(element)
                                }

                                setGatesDD()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {
                            tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
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

    private var dateSel = ""
    private var timeSel = ""

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
            context!!,
            android.R.layout.simple_spinner_item, gates
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        gateSpinner!!.adapter = adapter
        gateSpinner!!.setSelection(defGatePos)
        gateSpinner!!.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onFromDateSelected(date: String) {

    }

    override fun onToDateSelected(date: String) {

    }

    override fun onDateSelected(date: String) {
        dateTV!!.text = date
        dateSel = date
    }

    override fun onFromTimeSelected(time: String) {
        timeTV!!.text = time
        timeSel = time
    }

    override fun onToTimeSelected(time: String) {

    }

    private fun makeAction(visitor: VisitorListItem) {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(activity!!)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/ForceCheckoutApi"

        val postData = AdminActionPost()
        postData.requestID = visitor.requestID!!.toInt()
        postData.status = statusSel
        postData.gateCode = gateSel
        postData.statusReason = comment
        postData.statusDtTm = "${dateSel}T${timeSel}"

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(postData)
        )
        tokenRefreshSel = this
        apiCallable.doVisitorActionApi(
            url, prefUtil.getApiToken(),
            requestBody
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
                                afterLoad()

                                comment = ""
                                dateSel = ""
                                timeSel = ""
                                gateSel = ""

                                if (isDeptView) {
                                    //setDepartments()
                                    if (isFromFilter!!) {
                                        // setDepartments(deptList = deptList!!)
                                        getDeptAnalyticsApi()
                                    } else
                                        getDeptAnalyticsApi()
                                } else {
//            val visitorLogList = ArrayList<VisitorLogList>()
//            setVisitorsInDept(visitorLogList)

                                    getVisitorLogListApi()
                                }
                            } else {
                                afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {
                            tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                        }
                        response.code() == 500 -> {
                            afterLoad()
                            showToast("Server Error!")
                        }
                    }
                }

                override fun onFailure(call: Call<VisitorActionApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

}
