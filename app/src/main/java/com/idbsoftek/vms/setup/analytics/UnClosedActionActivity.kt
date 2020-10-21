package com.idbsoftek.vms.setup.analytics

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
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
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VisitorActionApiResponse
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.setup.log_list.VisitorListItem
import com.idbsoftek.vms.setup.log_list.VisitorLogItemClickable
import com.idbsoftek.vms.setup.log_list.VisitorLogListApiResponse
import com.idbsoftek.vms.setup.log_list.VistorLogListAdapter
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.setup.visitor_stats.StatsOfVisitorApiResponse
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.AugDatePicker
import com.idbsoftek.vms.util.DateTimeSelectable
import com.idbsoftek.vms.util.PrefUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnClosedActionActivity : VmsMainActivity(), AdminActionable, VisitorLogItemClickable,
    TokenRefreshable, AdapterView.OnItemSelectedListener,
    DateTimeSelectable {
    private var loader: ProgressBar? = null
    private var unClosedRV: RecyclerView? = null
    private var activity: UnClosedActionActivity? = null
    private var prefUtil: PrefUtil? = null

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_closed_action)

        activity = this
        prefUtil = PrefUtil(this)

        augDatePicker = AugDatePicker(activity!!, this)

        setActionBarTitle("Admin Closure")

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        loader = findViewById(R.id.unclosed_progress)
        unClosedRV = findViewById(R.id.unclosed_rv)
        unClosedRV!!.layoutManager = LinearLayoutManager(this)
        unClosedRV!!.setHasFixedSize(true)
    }

    private var fromDate: String? = ""
    private var toDate: String? = ""

    private fun getVisitorLogListApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/ForcedCheckOutList"
        tokenRefreshSel = this

        apiCallable.loadUnclosedRecords(
            url, prefUtil.getApiToken()
        ).enqueue(object : Callback<VisitorLogListApiResponse> {
            override fun onResponse(
                call: Call<VisitorLogListApiResponse>,
                response: Response<VisitorLogListApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                        val visitorLogApiResponse = response.body()
                        if (visitorLogApiResponse!!.status == true) {
                            setVisitorsInDept(visitorLogApiResponse.visitorList!!)
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

            override fun onFailure(call: Call<VisitorLogListApiResponse>, t: Throwable) {
                t.printStackTrace()
                afterLoad()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        getVisitorLogListApi()
    }

    private fun setVisitorsInDept(visitorsList: List<VisitorListItem>) {
        val adapter = VistorLogListAdapter(
            this,
            true,
            visitorLogList = visitorsList, this
        )
        unClosedRV!!.adapter = adapter
    }

    private fun onLoad() {
        unClosedRV!!.visibility = View.GONE
        loader!!.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        unClosedRV!!.visibility = View.VISIBLE
        loader!!.visibility = View.GONE
    }

    private var augDatePicker: AugDatePicker? = null

    private var rectBg: RelativeLayout? = null

    private fun showActionPopUp(visitor: VisitorListItem) {

        rejectSheetDialog = BottomSheetDialog(activity!!)
        val view: View = layoutInflater.inflate(R.layout.admin_action_popup, null)
        val commentTxtIP: TextInputLayout = view.findViewById(R.id.admin_comment_txt_ip)
        val submitBtn: MaterialButton = view.findViewById(R.id.submit_admin_btn)
        dateTV = view.findViewById(R.id.date_tv_admin_action_vms)
        timeTV = view.findViewById(R.id.time_admin_vms_form)

        gateTV = view.findViewById(R.id.gate_title_tv)
        rectBg = view.findViewById(R.id.gate_spinner_bg)

        statusSpinner = view.findViewById(R.id.status_spinner_admin)
        gateSpinner = view.findViewById(R.id.gate_spinner_admin)


        when (visitor.status) {
            VMSUtil.MeetStartAction -> {
                statusListDD.clear()
                visitStatusList.clear()
                var statusUtil = VMSUtil.Companion.StatusUtil(4, "Meet Completed")
                visitStatusList.add(statusUtil)

                statusUtil = VMSUtil.Companion.StatusUtil(VMSUtil.CheckOutAction, "Exit")
                visitStatusList.add(statusUtil)

                setStatusDD()
            }
            VMSUtil.MeetCompleteAction -> {
                statusListDD.clear()
                visitStatusList.clear()

                var statusUtil = VMSUtil.Companion.StatusUtil(VMSUtil.CheckOutAction, "Exit")
                visitStatusList.add(statusUtil)

                setStatusDD()
            }
            else -> {
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
            if (AppUtil.isInternetThere(activity!!)) {
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

    override fun onAdminAction(visitor: VisitorListItem) {
        showActionPopUp(visitor)
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

    override fun onNothingSelected(parent: AdapterView<*>?) {

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
                            tokenRefresh!!.doTokenRefresh(activity!!, tokenRefreshSel)
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
            activity!!,
            android.R.layout.simple_spinner_item, gates
        )
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        gateSpinner!!.adapter = adapter
        gateSpinner!!.setSelection(defGatePos)
        gateSpinner!!.onItemSelectedListener = this
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

                                    getVisitorLogListApi()
                            } else {
                                afterLoad()
                                showToast(response.body()!!.message!!)
                            }
                        }
                        response.code() == 401 -> {
                            tokenRefresh!!.doTokenRefresh(activity!!, tokenRefreshSel)
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

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
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

    override fun onTokenRefresh(responseCode: Int, token: String) {
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(activity!!)
            }
            200 -> {
                getVisitorLogListApi()
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
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


}