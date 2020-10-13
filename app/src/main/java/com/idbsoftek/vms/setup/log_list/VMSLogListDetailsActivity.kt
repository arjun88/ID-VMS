package com.idbsoftek.vms.setup.log_list

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VisitorActionApiResponse
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.form.AssociatesListFragment
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.PrefUtil
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VMSLogListDetailsActivity : VmsMainActivity(), AdapterView.OnItemSelectedListener,
    TokenRefreshable, AssociateActionReturnable {

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
    private var eligibilityTV: AppCompatTextView? = null
    private var associatesCountTV: AppCompatTextView? = null

    private var roleTV: AppCompatTextView? = null
    private var purposeTV: AppCompatTextView? = null
    private var dateTV: AppCompatTextView? = null
    private var timeTV: AppCompatTextView? = null
    private var reqStatusTV: AppCompatTextView? = null
    private var vehNumTV: AppCompatTextView? = null

    private var personalIDTV: AppCompatTextView? = null
    private var personalIDNumTV: AppCompatTextView? = null
    private var bodyTempTV: AppCompatTextView? = null

    private var detailsLoadedView: View? = null
    private var noDataView: View? = null

    private var refNum: String? = null
    private var prefUtil: PrefUtil? = null

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vmslog_list_details)
        setActionBarTitle("Details")

        context = this

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        //isForSecurity = intent.getBooleanExtra("IS_FOR_SECURITY", false)

        refNum = intent.getStringExtra("REF_NUM")

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
        bodyTempTV = findViewById(R.id.vms_details_temp_tv)
        eligibilityTV = findViewById(R.id.eligibility_tv)

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
            makeAction(VMSUtil.ApproveAction)
        }

        findViewById<MaterialButton>(R.id.reject_btn_details).setOnClickListener {
            showRejectPopUp(VMSUtil.RejectAction)
        }

        findViewById<MaterialButton>(R.id.complete_btn_details).setOnClickListener {
            makeAction(VMSUtil.MeetCompleteAction)
        }

        findViewById<MaterialButton>(R.id.session_out_btn_details).setOnClickListener {
            // makeAction(VMSUtil.SessionOutAction)
            showGatePickerPopUp(VMSUtil.SessionOutAction, refNum!!, true)
        }

        findViewById<MaterialButton>(R.id.session_in_btn_details).setOnClickListener {
            showGatePickerPopUp(VMSUtil.SessionInAction, refNum!!, true)

            // showMultiDayCheckInPopUp(VMSUtil.CheckInAction,refNum!!,true)
        }

        findViewById<MaterialButton>(R.id.meet_start_btn_details).setOnClickListener {
            makeAction(VMSUtil.MeetStartAction)
        }

        findViewById<MaterialButton>(R.id.allow_btn_details).setOnClickListener {
            // makeAction(VMSUtil.ALLOW_ACTION, refNum!!)

            if (!isMultiDayCheckIn)
                showGatePickerPopUp(VMSUtil.CheckInAction, refNum!!, true)
            else
                showMultiDayCheckInPopUp(VMSUtil.CheckInAction, refNum!!, true)
        }

        findViewById<MaterialButton>(R.id.exit_btn_details).setOnClickListener {
            // makeAction(VMSUtil.EXIT_ACTION, refNum!!)
            if (findViewById<MaterialButton>(R.id.exit_btn_details).text == "Cancel") {
                showRejectPopUp(-3)
            } else
                showGatePickerPopUp(VMSUtil.CheckOutAction, refNum!!, true)
        }

        buttonClickForVisitorLogic()
    }

    private var visitorID = 0

    private fun buttonClickForVisitorLogic() {
        findViewById<MaterialButton>(R.id.approve_btn_visitor_details).setOnClickListener {
            visitorActionApi(VMSUtil.ApproveAction, null)
        }

        findViewById<MaterialButton>(R.id.reject_btn_visitor_details).setOnClickListener {
            visitorActionApi(VMSUtil.RejectAction, null)
        }

        findViewById<MaterialButton>(R.id.complete_btn_visitor_details).setOnClickListener {
            visitorActionApi(VMSUtil.MeetCompleteAction, null)
        }

        findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).setOnClickListener {
            // makeAction(VMSUtil.SessionOutAction)
            showGatePickerPopUp(VMSUtil.SessionOutAction, refNum!!, false)
        }

        findViewById<MaterialButton>(R.id.session_in_btn_visitor_details).setOnClickListener {
            // makeAction(VMSUtil.SessionInAction)
            showGatePickerPopUp(VMSUtil.SessionInAction, refNum!!, false)
        }

        findViewById<MaterialButton>(R.id.meet_start_btn_visitor_details).setOnClickListener {
            visitorActionApi(VMSUtil.MeetStartAction, null)
        }

        findViewById<MaterialButton>(R.id.allow_btn_visitor_details).setOnClickListener {
            // makeAction(VMSUtil.ALLOW_ACTION, refNum!!)
            if (!isMultiDayCheckIn)
                showGatePickerPopUp(VMSUtil.CheckInAction, refNum!!, false)
            else
                showMultiDayCheckInPopUp(VMSUtil.CheckInAction, refNum!!, false)
        }

        findViewById<MaterialButton>(R.id.exit_btn_visitor_details).setOnClickListener {
            // makeAction(VMSUtil.EXIT_ACTION, refNum!!)
            showGatePickerPopUp(VMSUtil.CheckOutAction, refNum!!, false)
        }
    }

    private var isMultiDayCheckIn = false
    private var isAssociatesAdded = false

    //Multi Day Check In API

    private fun multiDayActionApi(action: Int, multiDayCheckInPost: MultiDayCheckInPost) {
        onAction()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/MultiEntry"

        //   val postData = MultiDayCheckInPost()
        multiDayCheckInPost.requestID = refNum!!.toInt()
        multiDayCheckInPost.status = action
        multiDayCheckInPost.gateCode = gateSel
        multiDayCheckInPost.visitorID = visitorID

        val gson = Gson()
        val requestBody: RequestBody = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(multiDayCheckInPost)
        )
        tokenRefreshSel = this
        apiCallable.doMultiDayActionApi(
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
                            showToast("Status Updated Successfully!")
                            afterLoad()
                            fetchDetailsApi()
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


    // SIngle action

    private fun visitorActionApi(action: Int, multiDayCheckInPost: MultiDayCheckInPost?) {
        onAction()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/UpdateSingleStatus"

        val postData = AssociateStatusPost()
        postData.requestID = refNum!!.toInt()
        postData.status = action
        postData.gateCode = gateSel
        postData.visitorID = visitorID

        if (multiDayCheckInPost != null) {
            postData.assetName = multiDayCheckInPost.assetName
            postData.assetNumber = multiDayCheckInPost.assetNumber
            postData.vehicleNumber = multiDayCheckInPost.vehicleNumber
            postData.bodyTemp = multiDayCheckInPost.bodyTemp
            postData.oxygenSaturation = multiDayCheckInPost.oxygenSaturation
        }

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

                            showToast("Status Updated Successfully!")
                            afterLoad()
                            fetchDetailsApi()
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

    override fun onStart() {
        super.onStart()
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

    //Reject Comment PopUp

    private var rejectSheetDialog: BottomSheetDialog? = null

    private fun showRejectPopUp(action: Int) {

        rejectSheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.reject_popup, null)
        val commentTxtIP: TextInputLayout = view.findViewById(R.id.reject_comment_txt_ip_form_vms)
        val titleTV: AppCompatTextView = view.findViewById(R.id.reject_pop_up_title_tv)

        if (action == -3) {
            titleTV.text = "Cancel Self Approval Request"
        } else {
            titleTV.text = "Reject"
        }

        rejectComment = commentTxtIP.editText!!.text.toString()
        view.findViewById<View>(R.id.gate_submit_btn)
            .setOnClickListener {
                rejectSheetDialog!!.dismiss()
                if (AppUtil.isInternetThere(context!!)) {
                    makeAction(action)
                    //
                } else {
                    showToast("No Internet!")
                }
            }

        rejectSheetDialog!!.setContentView(view)
        rejectSheetDialog!!.show()
    }

    private fun textChangeListener(textIP: TextInputLayout, isFromBodyTemp: Boolean) {
        textIP.editText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    if (isFromBodyTemp) {
                        when {
                            s.toString().toDouble() < VMSUtil.MIN_BODY_TEMP -> {
                                textIP.error = "Body Temperature is par below than MIN limit!"
                            }
                            s.toString().toDouble() > VMSUtil.MAX_BODY_TEMP -> {
                                textIP.isErrorEnabled = true
                                textIP.error = "Body Temperature is crossing MAX limit!"
                            }
                            else -> {
                                textIP.isErrorEnabled = false
                            }
                        }
                    } else {
                        when {
                            s.toString().toDouble() < VMSUtil.MIN_OXY_TEMP -> {
                                textIP.error = "OX is par below than MIN limit!"
                            }
                            s.toString().toDouble() > VMSUtil.MAX_OXY_TEMP -> {
                                textIP.isErrorEnabled = true
                                textIP.error = "OX is crossing MAX limit!"
                            }
                            else -> {
                                textIP.isErrorEnabled = false
                            }
                        }
                    }
                }
            }
        })

    }

    //**** Multi Day Check In

    private var multiDaySheetDialog: BottomSheetDialog? = null

    private fun showMultiDayCheckInPopUp(action: Int, id: String, isFromBulk: Boolean) {

        multiDaySheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.multi_day_checkin_popup, null)
        gateSpinner = view.findViewById(R.id.gate_spinner)

        val bodyTempTxtIP: TextInputLayout = view.findViewById(R.id.body_temp_txt_ip_form_vms)
        val oxyTxtIP: TextInputLayout = view.findViewById(R.id.pulse_rate_txt_ip_form_vms)
        val vehNumTxtIP: TextInputLayout = view.findViewById(R.id.id_veh_num_txt_ip_form_vms)
        val assetsNumTxtIP: TextInputLayout = view.findViewById(R.id.assets_count_txt_ip_form_vms)
        val assetsTxtIP: TextInputLayout = view.findViewById(R.id.assets_txt_ip_form_vms)

        textChangeListener(bodyTempTxtIP, true)
        textChangeListener(oxyTxtIP, false)

        getGatesApi()

        view.findViewById<View>(R.id.gate_submit_btn)
            .setOnClickListener {
                val vehNum = vehNumTxtIP.editText!!.text.toString()
                val assetsNum = assetsNumTxtIP.editText!!.text.toString()
                val assetsCarried = assetsTxtIP.editText!!.text.toString()

                var assentNumb = 0
                if (assetsNum.isNotEmpty())
                    assentNumb = assetsNum.toInt()

                val postData = MultiDayCheckInPost()
                postData.assetName = assetsCarried
                postData.assetNumber = assentNumb
                postData.bodyTemp = bodyTempTxtIP.editText!!.text.toString()
                postData.oxygenSaturation = oxyTxtIP.editText!!.text.toString()
                postData.vehicleNumber = vehNum

                if (gateSel!!.isNotEmpty()) {
                    multiDaySheetDialog!!.dismiss()
                    if (AppUtil.isInternetThere(context!!)) {
                        if (isFromBulk)
                            multiDayActionApi(
                                VMSUtil.CheckInAction,
                                postData
                            )
                        //  makeAction(action)
                        else
                            visitorActionApi(action, postData)

                    } else {
                        showToast("No Internet!")
                    }
                } else {
                    showToast("Please wait until the gates list are shown!")
                }
            }

        multiDaySheetDialog!!.setContentView(view)
        multiDaySheetDialog!!.show()
    }

    // ********** GATE CONCEPT

    private var gateSheetDialog: BottomSheetDialog? = null

    private fun showGatePickerPopUp(action: Int, id: String, isFromBulk: Boolean) {

        gateSheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.gate_popup, null)
        gateSpinner = view.findViewById(R.id.gate_spinner)

        getGatesApi()

        view.findViewById<View>(R.id.gate_submit_btn)
            .setOnClickListener {
                gateSheetDialog!!.dismiss()
                if (AppUtil.isInternetThere(context!!)) {
                    if (isFromBulk)
                        makeAction(action)
                    else
                        visitorActionApi(action, null)

                } else {
                    showToast("No Internet!")
                }
            }

        gateSheetDialog!!.setContentView(view)
        gateSheetDialog!!.show()
    }

    private var gateSel: String? = ""
    private var rejectComment: String? = ""
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
                .dontAnimate()
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
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/VMCListRequestId"
        tokenRefreshSel = this
        apiCallable.fetchLogDetailsApi(
            url, refNum, prefUtil.getApiToken()
        ).enqueue(object : Callback<VisitorLogDetailsApiResponse> {
            override fun onResponse(
                call: Call<VisitorLogDetailsApiResponse>,
                response: Response<VisitorLogDetailsApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        val visitorLogApiResponse = response.body()
                        if (visitorLogApiResponse!!.status == true) {
                            afterLoad()
                            setDetailsView(
                                visitorLogApiResponse.logDetails!!,
                                visitorLogApiResponse.logDetails!!.associateDetails!! as ArrayList<AscRecord>
                            )
                        } else {
                            afterLoad()
                            detailsLoadedView!!.visibility = View.GONE
                            showToast(response.body()!!.message!!)
                        }
                    }
                    response.code() == 401 -> {
                        tokenRefresh!!.doTokenRefresh(context!!, tokenRefreshSel)
                    }
                    response.code() == 500 -> {
                        afterLoad()
                        detailsLoadedView!!.visibility = View.GONE
                        showToast("Server Error!")
                    }
                }
            }

            override fun onFailure(call: Call<VisitorLogDetailsApiResponse>, t: Throwable) {
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

    @SuppressLint("SetTextI18n")
    private fun setDetailsView(
        visitDetail: VisitorListItem,
        associatesAddedList: ArrayList<AscRecord>
    ) {
        visitorID = visitDetail.visitorID!!
        refNumTV!!.text = "Ref Num: ${visitDetail.requestID}"
        nameTV!!.text = "Name: ${visitDetail.visitorName}"
        mobTV!!.text = "Mob: ${visitDetail.visitorMobile}"
        toMeetTV!!.text = "To Meet: ${visitDetail.employeeFullName}"
        vehNumTV!!.text = "Vehicle Num: ${visitDetail.vehicleNumber}" //${}

        purposeTV!!.text = "Purpose: ${visitDetail.purposeName}"
        deptTV!!.text = "Department: ${visitDetail.departmentCode}"
        roleTV!!.text = "Designation: ${visitDetail.designationCode}"
        bodyTempTV!!.text = "Body Temperature: ${visitDetail.bodyTemp}"
        timeTV!!.text = "Visit Category: ${visitDetail.categoryName}"
        deptTV!!.visibility = View.VISIBLE
        roleTV!!.visibility = View.VISIBLE

        eligibilityTV!!.text = "Comment: "

        compTV!!.text = "Company: ${visitDetail.visitorCompany}"
        dateTV!!.text = "Date: ${visitDetail.fromDate} to ${visitDetail.toDate}"
        //timeTV!!.text = "Timings: ${visitDetail.time}"

        personalIDTV!!.text = "${visitDetail.iDProofCode}"
        personalIDNumTV!!.text = "${visitDetail.proofDetails}"


        val numOfAss = associatesAddedList.size

        associatesCountTV!!.text = "$numOfAss Associates added"
        if (numOfAss > 0) {
            associatesCountTV!!.setOnClickListener {
                moveToAssociatesFragment(associatesAddedList as ArrayList<AscRecord>)
            }
            showVisitorBtnViewBasedOnStatus(visitDetail.movementStatus.toString())
        } else {
            clearAllActions(false)
        }

        showBtnViewBasedOnStatus(visitDetail.status.toString())
        val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${visitDetail.imageName!!}"
        setImage(fullUrl)
        visitorIV!!.setOnClickListener {
            showImagePopUp(this, fullUrl)
        }

        if (PrefUtil.getVmsEmpROle() == "approver") {
            if (visitDetail.isSelfApproved!! && visitDetail.status != -3) {
                clearAllActions(true)
                val exitButton: MaterialButton = findViewById(R.id.exit_btn_details)
                exitButton.visibility = View.VISIBLE
                exitButton.text = "Cancel"
            }
        }

        /*if (visitDetail.imageData != null)
            if (visitDetail.imageData.isNotEmpty())
                loadImage(visitorIV, visitDetail.imageData)*/
    }

    private fun loadImage(image: CircleImageView?, base64String: String) {
        try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            image!!.setImageBitmap(decodedImage)
        } catch (e: Exception) {

        }

    }

    private fun onLoad() {
        loadingView!!.visibility = View.VISIBLE
        detailsLoadedView!!.visibility = View.GONE
        noDataView!!.visibility = View.GONE
    }

    private fun moveToAssociatesFragment(associatesAddedList: ArrayList<AscRecord>) {
        val fragment =
            AssociatesListFragment()

        fragment.initActionListener(this)

        val arg = Bundle()
        arg.putParcelableArrayList("ASSOCIATES", associatesAddedList)
        arg.putBoolean("IS_FORM", false)
        arg.putInt("REQ_ID", refNum!!.toInt())
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
    private fun showVisitorBtnViewBasedOnStatus(status: String?) {
        when {
            PrefUtil.getVmsEmpROle().toLowerCase() == "admin" -> {

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.approve_btn_visitor_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.reject_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(false)
                        /*reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED*/
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.allow_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.meet_start_btn_visitor_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.session_in_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                         val redColor =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                         reqStatusTV!!.setTextColor(redColor)
                         reqStatusTV!!.text = "Checked Out"*/
                    }
                    VMSUtil.MeetStartAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.complete_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    /*EXPIRED -> {
                        Log.e("EXPIRED", "Status")
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Expired"
                    }*/
                    VMSUtil.MultiDayCheckIn -> {
                        this.isMultiDayCheckIn = true
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.allow_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    else -> {
                        clearAllActions(false)
                    }
                }
            }
            PrefUtil.getVmsEmpROle().toLowerCase() == "security" -> {
                //V 2.0

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_blue_border)
                         val redColor =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                         reqStatusTV!!.setTextColor(redColor)
                         reqStatusTV!!.text = "Pending"*/
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                         val redColor =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                         reqStatusTV!!.setTextColor(redColor)
                         reqStatusTV!!.text = REJECTED*/
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.allow_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(false)


                        /* findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                             View.VISIBLE*/

                        /*reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_blue_border)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Checked In"*/
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).visibility =
                            View.VISIBLE

                        if (!prefUtil!!.isMeetCompleteReq()) {
                            findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility =
                                View.VISIBLE
                        }
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.session_in_btn_visitor_details).visibility =
                            View.VISIBLE

                        if (!prefUtil!!.isMeetCompleteReq()) {
                            findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility =
                                View.VISIBLE
                        }
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                         val redColor =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                         reqStatusTV!!.setTextColor(redColor)
                         reqStatusTV!!.text = "Checked Out"*/
                    }
                    VMSUtil.MeetStartAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).visibility =
                            View.VISIBLE

                        if (!prefUtil!!.isMeetCompleteReq()) {
                            findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility =
                                View.VISIBLE
                        }
                    }
                    VMSUtil.MultiDayCheckIn -> {
                        this.isMultiDayCheckIn = true
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.allow_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    else -> {
                        clearAllActions(false)
                    }
                }

            }
            else -> {

                //V 2.0

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(false)
                        /* findViewById<MaterialButton>(R.id.approve_btn_visitor_details).visibility =
                             View.VISIBLE
                         findViewById<MaterialButton>(R.id.reject_btn_visitor_details).visibility =
                             View.VISIBLE*/
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(false)
                        /*reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED*/
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(false)

                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                         val color =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                         reqStatusTV!!.setTextColor(color)
                         reqStatusTV!!.text = "Approved"*/
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.meet_start_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.complete_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetStartAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.complete_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(false)
                        findViewById<MaterialButton>(R.id.complete_btn_visitor_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_corners_blue_bg)
                         val color =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                         reqStatusTV!!.setTextColor(color)
                         reqStatusTV!!.text = "Checked Out"*/
                    }

                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(false)
                        /* reqStatusTV!!.visibility = View.VISIBLE
                         reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                         val color =
                             ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                         reqStatusTV!!.setTextColor(color)
                         reqStatusTV!!.text = "Meet Completed"*/
                    }
                    else -> {
                        clearAllActions(false)
                    }
                }

            }
        }
    }


    @SuppressLint("DefaultLocale")
    private fun showBtnViewBasedOnStatus(status: String?) {
        this.isMultiDayCheckIn = false
        when {
            PrefUtil.getVmsEmpROle().toLowerCase() == "admin" -> {

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.approve_btn_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.reject_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.meet_start_btn_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.session_in_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Checked Out"
                    }

                    VMSUtil.MeetStartAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                            View.VISIBLE
                    }
                    /*EXPIRED -> {
                        Log.e("EXPIRED", "Status")
                        clearAllActions()
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Expired"
                    }*/
                    VMSUtil.MultiDayCheckIn -> {
                        this.isMultiDayCheckIn = true
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    else -> {
                        clearAllActions(true)
                    }
                }
            }
            PrefUtil.getVmsEmpROle().toLowerCase() == "security" -> {
                //V 2.0

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_blue_border)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Pending"
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    VMSUtil.CancelSelfApproval -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Cancelled"
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(true)
                        /* findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                             View.VISIBLE*/

                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_blue_border)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Checked In"
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                            View.VISIBLE

                        if (!prefUtil!!.isMeetCompleteReq()) {
                            findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                                View.VISIBLE
                        }
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.session_in_btn_details).visibility =
                            View.VISIBLE

                        if (!prefUtil!!.isMeetCompleteReq()) {
                            findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                                View.VISIBLE
                        }
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Checked Out"
                    }
                    VMSUtil.MeetStartAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MultiDayCheckIn -> {
                        clearAllActions(true)
                        this.isMultiDayCheckIn = true
                        findViewById<MaterialButton>(R.id.allow_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.exit_btn_details).visibility =
                            View.VISIBLE
                    }
                    else -> {
                        clearAllActions(true)
                    }
                }

                //
                /*  when (status) {
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
                              ContextCompat.getColor(
                                  this@VMSLogListDetailsActivity,
                                  R.color.green
                              )
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
                  }*/
            }
            else -> {

                //V 2.0

                when (status!!.toInt()) {
                    VMSUtil.PendingAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.approve_btn_details).visibility =
                            View.VISIBLE
                        findViewById<MaterialButton>(R.id.reject_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CancelSelfApproval -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = "Cancelled"
                    }
                    VMSUtil.RejectAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                        val redColor =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.red)
                        reqStatusTV!!.setTextColor(redColor)
                        reqStatusTV!!.text = REJECTED
                    }
                    VMSUtil.ApproveAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                        val color =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                        reqStatusTV!!.setTextColor(color)
                        reqStatusTV!!.text = "Approved"
                    }
                    VMSUtil.CheckInAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.meet_start_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionInAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.SessionOutAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.CheckOutAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_corners_blue_bg)
                        val color =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.blue)
                        reqStatusTV!!.setTextColor(color)
                        reqStatusTV!!.text = "Checked Out"
                    }
                    VMSUtil.MeetStartAction -> {
                        clearAllActions(true)
                        findViewById<MaterialButton>(R.id.complete_btn_details).visibility =
                            View.VISIBLE
                    }
                    VMSUtil.MeetCompleteAction -> {
                        clearAllActions(true)
                        reqStatusTV!!.visibility = View.VISIBLE
                        reqStatusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                        val color =
                            ContextCompat.getColor(this@VMSLogListDetailsActivity, R.color.green)
                        reqStatusTV!!.setTextColor(color)
                        reqStatusTV!!.text = "Meet Completed"
                    }
                    else -> {
                        clearAllActions(true)
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

    private fun makeAction(action: Int) {
        onAction()
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(this)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/UpdateStatus"

        val postData = UpdateLogStatusPost()
        postData.requestID = refNum!!.toInt()
        postData.status = action
        postData.gateCode = gateSel
        postData.rejectComment = rejectComment

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

                                fetchDetailsApi()
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

    private fun clearAllActions(isFromBulk: Boolean) {
        if (isFromBulk) {
            findViewById<MaterialButton>(R.id.approve_btn_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.reject_btn_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.complete_btn_details).visibility = View.GONE

            findViewById<MaterialButton>(R.id.allow_btn_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.exit_btn_details).visibility = View.GONE

            findViewById<MaterialButton>(R.id.session_out_btn_details).visibility =
                View.GONE
            findViewById<MaterialButton>(R.id.session_in_btn_details).visibility =
                View.GONE
            findViewById<MaterialButton>(R.id.meet_start_btn_details).visibility =
                View.GONE
        } else {
            findViewById<MaterialButton>(R.id.approve_btn_visitor_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.reject_btn_visitor_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.complete_btn_visitor_details).visibility = View.GONE

            findViewById<MaterialButton>(R.id.allow_btn_visitor_details).visibility = View.GONE
            findViewById<MaterialButton>(R.id.exit_btn_visitor_details).visibility = View.GONE

            findViewById<MaterialButton>(R.id.session_out_btn_visitor_details).visibility =
                View.GONE
            findViewById<MaterialButton>(R.id.session_in_btn_visitor_details).visibility =
                View.GONE
            findViewById<MaterialButton>(R.id.meet_start_btn_visitor_details).visibility =
                View.GONE
        }
        reqStatusTV!!.visibility = View.GONE
    }

    override fun onTokenRefresh(responseCode: Int, token: String) {
        afterLoad()
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(context!!)
            }
            200 -> {
                fetchDetailsApi()
            }
            else -> {
                AppUtil.onSessionOut(context!!)
            }
        }
    }

    override fun onAssociateActionReturn() {
        fetchDetailsApi()
    }


}
