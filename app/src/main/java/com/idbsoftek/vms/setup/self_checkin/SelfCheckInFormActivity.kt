package com.idbsoftek.vms.setup.self_checkin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.button.MaterialButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.RefNumListApiResponse
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VisitorListItem
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.GatesListingItem
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelfCheckInFormActivity : VmsMainActivity(), AdapterView.OnItemSelectedListener {
    private var activity: SelfCheckInFormActivity? = null
    private var nameTV: AppCompatTextView? = null
    private var statusTV: AppCompatTextView? = null
    private var toMeetTV: AppCompatTextView? = null

    private var refNumSpinner: AppCompatSpinner? = null
    private var refNumList = ArrayList<VisitorListItem>()
    private var refNumForDD = ArrayList<String>()
    private var refNumSel: String = ""

    private var submitBtn: MaterialButton? = null
    private var progress: ProgressBar? = null

    private var SUBMIT_BTN_TEXT = "Check In"
    private var curStatus = "Approved"
    private var veID = ""
    private var ACTION = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_check_in_form)

        activity = this
        setActionBarTitle("Quick Check In / Out")

        initView()
    }

    private fun initView() {
        nameTV = findViewById(R.id.vis_name_tv_checkInForm)
        statusTV = findViewById(R.id.status_tv_checkInForm)
        toMeetTV = findViewById(R.id.to_meet_tv_checkInForm)

        progress = findViewById(R.id.progress_checkInForm)
        refNumSpinner = findViewById(R.id.ref_num_spinner_checkInform)
        gateSpinner = findViewById(R.id.gate_spinner_checkInform)

        submitBtn = findViewById(R.id.submit_btn_checkInForm)
        submitBtn!!.setOnClickListener {
            when (curStatus) {
                "Approved" -> ACTION = "CheckIn"
                "Completed" -> ACTION = "CheckOut"
            }

            //CALL API
            if (AppUtil.isInternetThere(activity!!)) {
                doSelfCheckIn()
            } else {
                showToast("No Internet!")
            }
        }
    }

    private fun setDetails(details: RefNumDetailsApiResponse) {
        nameTV!!.text = details.name
        toMeetTV!!.text = details.toMeet
        statusTV!!.text = details.curStatus

        curStatus = details.curStatus!!
        veID = details.veID!!

        toggleBtn()
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        getGatesApi()
        getRefNumApi()
        toggleBtn()
    }

    private fun onLoad() {
        progress!!.visibility = View.VISIBLE
        submitBtn!!.visibility = View.GONE
    }

    private fun afterLoad() {
        progress!!.visibility = View.GONE
        submitBtn!!.visibility = View.VISIBLE
    }

    private fun toggleBtn() {
        submitBtn!!.visibility = View.VISIBLE
        when (curStatus) {
            "Approved" -> SUBMIT_BTN_TEXT = "Check In"
            "Completed" -> SUBMIT_BTN_TEXT = "Check Out"
            else -> submitBtn!!.visibility = View.GONE
        }

        submitBtn!!.text = SUBMIT_BTN_TEXT
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
            url, prefUtil.userName, prefUtil.sessionID
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

    private var gatesList: ArrayList<GatesListingItem> = ArrayList()

    private var gates: ArrayList<String> = ArrayList()

    @SuppressLint("DefaultLocale")
    private fun setRefNumDD() {
        for (i in 0 until refNumList.size) {
            val name = refNumList[i].visitorRefNum!!

            refNumForDD.add(name)
        }

        val adapter = ArrayAdapter(
            activity!!,
            android.R.layout.simple_spinner_item, refNumForDD
        )

        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        refNumSpinner!!.adapter = adapter
        refNumSpinner!!.onItemSelectedListener = this
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

    private fun getRefNumApi() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}VisitorSecurityList"

        apiCallable.getRefNumList(
            url, prefUtil.userName, prefUtil.sessionID
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

    // GET DETAILS

    private fun getRefNumDetails() {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}QuickVisitorReference"

        apiCallable.getVisNumDetails(
            url, prefUtil.userName, prefUtil.sessionID, refNumSel
        )
            .enqueue(object : Callback<RefNumDetailsApiResponse> {
                override fun onResponse(
                    call: Call<RefNumDetailsApiResponse>,
                    response: Response<RefNumDetailsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                setDetails(visitorLogApiResponse)

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

                override fun onFailure(call: Call<RefNumDetailsApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    // afterLoad()
                }
            })
    }

    //


    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent) {
            gateSpinner -> {
                gateSel = gatesList[position].code
            }
            refNumSpinner -> {
                refNumSel = refNumList[position].visitorRefNum!!

                getRefNumDetails()
            }
        }
    }

    // DO SELF CHECK IN

    private fun doSelfCheckIn() {

        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(this)
        val url = "${prefUtil.appBaseUrl}QuickCheckInOut"

        apiCallable.doSelfCheckIn(
            url, prefUtil.userName,
            prefUtil.sessionID, refNumSel,
            veID,
            gateSel,
            ACTION
        )
            .enqueue(object : Callback<CommonApiResponse> {
                override fun onResponse(
                    call: Call<CommonApiResponse>,
                    response: Response<CommonApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {

                                showToast(response.body()!!.message!!)

                                afterLoad()
                                finish()
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

                override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

}
