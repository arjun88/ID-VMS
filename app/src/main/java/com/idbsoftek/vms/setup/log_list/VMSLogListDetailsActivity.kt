package com.idbsoftek.vms.setup.log_list

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.form.AssociatesListFragment
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.PrefUtil
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VMSLogListDetailsActivity : VmsMainActivity(), AdapterView.OnItemSelectedListener {

    //  private var isForSecurity: Boolean = false

//    For views

    private var context: VMSLogListDetailsActivity? = null

    private var loadingView: ProgressBar? = null
    private var visitorIV: CircleImageView? = null
    private var nameTV: AppCompatTextView? = null
    private var mobTV: AppCompatTextView? = null
    private var refNumTV: AppCompatTextView? = null
    private var toMeetTV: AppCompatTextView? = null
    private var compTV: AppCompatTextView? = null
    private var deptTV: AppCompatTextView? = null
    private var associatesCountTV: AppCompatTextView? = null

    private var roleTV: AppCompatTextView? = null
    private var purposeTV: AppCompatTextView? = null
    private var dateTV: AppCompatTextView? = null
    private var timeTV: AppCompatTextView? = null
    private var reqStatusTV: AppCompatTextView? = null
    private var vehNumTV: AppCompatTextView? = null

    private var personalIDTV: AppCompatTextView? = null
    private var personalIDNumTV: AppCompatTextView? = null

    private var detailsLoadedView: View? = null
    private var noDataView: View? = null

    private var refNum: String? = null
    private var prefUtil: PrefUtil? = null
    private var visitDate: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vmslog_list_details)
        setActionBarTitle("Details")

        context = this

        //isForSecurity = intent.getBooleanExtra("IS_FOR_SECURITY", false)
        refNum = intent.getStringExtra("REF_NUM")
        visitDate = intent.getStringExtra("VISIT_DATE")

        prefUtil = PrefUtil(this)
        initView()
        //showButtonsBasedOnUserType()
    }

    private fun initView() {
        associatesCountTV = findViewById(R.id.associate_added_count_tv)
        noDataView = findViewById(R.id.no_data_found_view)
        loadingView = findViewById(R.id.details_loading_vms)
        detailsLoadedView = findViewById(R.id.details_view_vms)

        visitorIV = findViewById(R.id.vms_details_image)

        vehNumTV = findViewById(R.id.vms_details_veh_num_tv)
        refNumTV = findViewById(R.id.vms_details_pass_num_tv)
        nameTV = findViewById(R.id.vms_details_name_tv)
        mobTV = findViewById(R.id.vms_details_mob_tv)

        reqStatusTV = findViewById(R.id.details_status_tv)

        toMeetTV = findViewById(R.id.vms_details_to_meet_tv)
        compTV = findViewById(R.id.vms_details_comp_tv)
        deptTV = findViewById(R.id.vms_details_dep_tv)
        roleTV = findViewById(R.id.vms_details_role_tv)

        purposeTV = findViewById(R.id.vms_details_purpose_tv)
        dateTV = findViewById(R.id.vms_details_date_tv)
        timeTV = findViewById(R.id.vms_details_time_tv)
        personalIDTV = findViewById(R.id.vms_details_id_tv)
        personalIDNumTV = findViewById(R.id.vms_details_id_num_tv)

        findViewById<MaterialButton>(R.id.approve_btn_details).setOnClickListener {
            makeAction(VMSUtil.APPROVE_ACTION, refNum!!)
        }

        findViewById<MaterialButton>(R.id.reject_btn_details).setOnClickListener {
            makeAction(VMSUtil.REJECT_ACTION, refNum!!)
        }

        findViewById<MaterialButton>(R.id.complete_btn_details).setOnClickListener {
            makeAction(VMSUtil.COMPLETE_ACTION, refNum!!)
        }

        findViewById<MaterialButton>(R.id.allow_btn_details).setOnClickListener {
            // makeAction(VMSUtil.ALLOW_ACTION, refNum!!)
            showGatePickerPopUp(VMSUtil.ALLOW_ACTION, refNum!!)
        }

        findViewById<MaterialButton>(R.id.exit_btn_details).setOnClickListener {
            // makeAction(VMSUtil.EXIT_ACTION, refNum!!)
            showGatePickerPopUp(VMSUtil.EXIT_ACTION, refNum!!)
        }

        if (AppUtil.isInternetThere(this)) {
            fetchDetailsApi()
        } else {
            showToast("No Internet!")
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0) {
            gateSpinner -> {
                gateSel = gatesList[p2].code!!
            }
        }
    }

    // ********** GATE CONCEPT

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

    private var gateSel: String? = null
    private var gateSpinner: AppCompatSpinner? = null

    private var gatesList: ArrayList<GatesListingItem> = ArrayList()

    private var gates: ArrayList<String> = ArrayList()

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

    private fun setImage(url: String) {
        try {
            Glide
                .with(this@VMSLogListDetailsActivity)
                .load(url)
                .placeholder(R.drawable.account)
                .apply(RequestOptions.placeholderOf(R.drawable.account).error(R.drawable.account))
                .into(visitorIV!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //    AppUtil.APP_BASE_URL_FOR_VMS
    private fun fetchDetailsApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        // val url = "${prefUtil.appBaseUrl}
        val url = "${prefUtil.appBaseUrl}VisitorLog"

        apiCallable.fetchDetails(
            url, prefUtil.userName, prefUtil.userName,
            refNum, visitDate
        )
            .enqueue(object : Callback<VMSDetailsApiResponse> {
                override fun onResponse(
                    call: Call<VMSDetailsApiResponse>,
                    response: Response<VMSDetailsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                afterLoad()
                                setDetailsView(
                                    visitorLogApiResponse.visitDetails!![0],
                                    visitorLogApiResponse.accompliceList!!
                                )
                            } else {
                                afterLoad()
                                detailsLoadedView!!.visibility = View.GONE
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                            detailsLoadedView!!.visibility = View.GONE
                            showToast("Server Error!")
                        }
                    }
                }

                override fun onFailure(call: Call<VMSDetailsApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isApprover(): Boolean {
        var isApprover = false

        if (PrefUtil.getVmsEmpROle() == "approver") {
            isApprover = true
        }

        return isApprover
    }

    private fun setDetailsView(
        visitDetail: VisitDetail,
        associatesAddedList: ArrayList<AssociatesItem>
    ) {
        refNumTV!!.text = "Ref Num: ${visitDetail.refNum}"
        nameTV!!.text = "Name: ${visitDetail.name}"
        mobTV!!.text = "Mob: ${visitDetail.mob}"
        toMeetTV!!.text = "To Meet: ${visitDetail.toMeet}"
        vehNumTV!!.text = "Vehicle Num: ${visitDetail.vehNum}"

        purposeTV!!.text = "Purpose: ${visitDetail.purpose}"
        deptTV!!.text = "Department: ${visitDetail.dept}"
        roleTV!!.text = "Designation: ${visitDetail.designation}"
        deptTV!!.visibility = View.VISIBLE
        roleTV!!.visibility = View.VISIBLE

        compTV!!.text = "Company: ${visitDetail.company}"
        dateTV!!.text = "Date: ${visitDetail.date}"
        timeTV!!.text = "Timings: ${visitDetail.time}"

        personalIDTV!!.text = "${visitDetail.idProof}"
        personalIDNumTV!!.text = "${visitDetail.idNumber}"

        val numOfAss = associatesAddedList.size

        associatesCountTV!!.text = "${numOfAss} Associates added"
        if (numOfAss > 0) {
            associatesCountTV!!.setOnClickListener {
                moveToAssociatesFragment(associatesAddedList)
            }
        }

        showBtnViewBasedOnStatus(visitDetail.status)
        val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${visitDetail.visitorImg!!}"
        setImage(fullUrl)
        visitorIV!!.setOnClickListener {
            showImagePopUp(this, fullUrl)
        }
    }

    private fun onLoad() {
        loadingView!!.visibility = View.VISIBLE
        detailsLoadedView!!.visibility = View.GONE
        noDataView!!.visibility = View.GONE
    }

    private fun moveToAssociatesFragment(associatesAddedList: ArrayList<AssociatesItem>) {
        val fragment =
            AssociatesListFragment()

        val arg = Bundle()
        arg.putParcelableArrayList("ASSOCIATES", associatesAddedList)
        arg.putBoolean("IS_FORM", false)
        fragment.arguments = arg

        val fm = supportFragmentManager
        val fT = fm.beginTransaction()
        fT.replace(R.id.vms_details_view, fragment)
        fT.addToBackStack(null)
        fT.commit()
    }

    private fun onNoData() {
        loadingView!!.visibility = View.GONE
        detailsLoadedView!!.visibility = View.GONE
        noDataView!!.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        loadingView!!.visibility = View.GONE
        detailsLoadedView!!.visibility = View.VISIBLE
        noDataView!!.visibility = View.GONE
    }

//    private fun showButtonsBasedOnUserType() {
//        if (isForSecurity) {
//            findViewById<MaterialButton>(R.id.approve_btn_details).visibility = View.GONE
//            findViewById<MaterialButton>(R.id.reject_btn_details).visibility = View.GONE
//            findViewById<MaterialButton>(R.id.complete_btn_details).visibility = View.GONE
//
//            findViewById<MaterialButton>(R.id.allow_btn_details).visibility = View.VISIBLE
//            findViewById<MaterialButton>(R.id.exit_btn_details).visibility = View.GONE
//        } else {
//            findViewById<MaterialButton>(R.id.approve_btn_details).visibility = View.VISIBLE
//            findViewById<MaterialButton>(R.id.reject_btn_details).visibility = View.VISIBLE
//            findViewById<MaterialButton>(R.id.complete_btn_details).visibility = View.GONE
//
//            findViewById<MaterialButton>(R.id.allow_btn_details).visibility = View.GONE
//            findViewById<MaterialButton>(R.id.exit_btn_details).visibility = View.GONE
//        }
//    }

    private val APPROVE_BTN_ENABLED = "Pending"
    private val COMPLETED_BTN_ENABLED = "CheckIn"
    private val ADMIN_COMPLETED_BTN_ENABLED = "CheckIn"
    private val ADMIN_ALLOW_BTN_ENABLED = "Approved"
    private val ALLOW_BTN_ENABLED = "Approved"
    private val REJECTED = "Rejected"
    private val EXPIRED = "Expired"
    private val COMPLETED = "Completed"
    private val EXIT_BTN_ENABLED = "CheckIn"
    private val ADMIN_EXIT_BTN_ENABLED = "Completed"

    @SuppressLint("DefaultLocale")
    private fun showBtnViewBasedOnStatus(status: String?) {

        when {
            PrefUtil.getVmsEmpROle().toLowerCase() == "admin" -> {
                when (status) {
                    APPROVE_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.approve_btn_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.reject_btn_details).visibility =
                            View.VISIBLE
                    }
                    REJECTED -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    ADMIN_ALLOW_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    ADMIN_COMPLETED_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    ADMIN_EXIT_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                            View.VISIBLE
                    }
                    EXPIRED -> {
                        Log.e("EXPIRED", "Status")
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Expired"
                    }
                    else -> {
                        clearAllActions()
                    }
                }
            }
            PrefUtil.getVmsEmpROle().toLowerCase() == "security" -> {
                when (status) {
                    "Pending" -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Pending"
                    }
                    "Future" -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = status
                    }
                    "Approved" -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    "CheckIn" -> {
                        clearAllActions()

                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Allowed"
                    }
                    "Completed" -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                            View.VISIBLE
                    }
                    "Exit" -> {
                        clearAllActions()
                    }
                    REJECTED -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    EXPIRED -> {
                        Log.e("EXPIRED", "Status")
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Expired"
                    }
                }
            }
            else -> {
                when (status) {
                    APPROVE_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.approve_btn_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.reject_btn_details).visibility =
                            View.VISIBLE
                    }
                    COMPLETED_BTN_ENABLED -> {
                        clearAllActions()
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    REJECTED -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    "Future" -> {
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = status
                    }
                    "Approved" -> {
                        clearAllActions()
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                        val greenColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                        reqStatusTV!!.setTextColor(greenColor)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.text = "Approved"
                    }
                    COMPLETED -> {
                        clearAllActions()
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                        val greenColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                        reqStatusTV!!.setTextColor(greenColor)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.text = COMPLETED
                    }
                    EXPIRED -> {
                        Log.e("EXPIRED", "Status")
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Expired"
                    }
                    else -> {
                        clearAllActions()
                    }
                }
            }
        }
    }

    // ACTIONS API **************

    private fun onAction() {
        loadingView!!.visibility = View.VISIBLE
        detailsLoadedView!!.visibility = View.VISIBLE
        noDataView!!.visibility = View.GONE
    }

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
                                afterLoad()

                                fetchDetailsApi()
                            } else {
                                afterLoad()
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
                    afterLoad()
                }
            })
    }

    private fun clearAllActions() {
        findViewById<MaterialButton>(R.id.approve_btn_details).visibility = View.GONE
        findViewById<MaterialButton>(R.id.reject_btn_details).visibility = View.GONE
        findViewById<MaterialButton>(R.id.complete_btn_details).visibility = View.GONE

        findViewById<MaterialButton>(R.id.allow_btn_details).visibility = View.GONE
        findViewById<MaterialButton>(R.id.exit_btn_details).visibility = View.GONE

        reqStatusTV!!.visibility = View.GONE
    }
}
