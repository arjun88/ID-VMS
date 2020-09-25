package com.idbsoftek.vms.setup.form

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VisitorActionApiResponse
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.log_list.AscRecord
import com.idbsoftek.vms.setup.log_list.AssociateStatusPost
import com.idbsoftek.vms.setup.log_list.VMSLogListDetailsActivity
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.PrefUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssociatesListFragment : Fragment(), AssociatesRemovable, TokenRefreshable,
    AdapterView.OnItemSelectedListener, AssociatesActionable {
    private var activity: AppCompatActivity? = null
    private var reqFormActivity: VisitReqFormActivity? = null
    private var detailsActivity: VMSLogListDetailsActivity? = null
    private var associatesRV: RecyclerView? = null
    private var viewa: View? = null
    private var loading: ProgressBar? = null
    private var isForm: Boolean? = false
    private var reqID: Int? = 0
    private var gateSel = ""
    private var ascItem: AscRecord? = null

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    private var associatesList: List<AscRecord> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewa = inflater.inflate(R.layout.fragment_department_wise_analytics, container, false)

        val arg = arguments
        associatesList = arg!!.getParcelableArrayList("ASSOCIATES")!!
        reqID = arg.getInt("REQ_ID")

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        initView()
        return viewa
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as AppCompatActivity?

        activity!!.supportActionBar!!.title = "Associates"
    }

    private var gateSheetDialog: BottomSheetDialog? = null

    private fun showGatePickerPopUp(action: Int, asc: AscRecord) {

        gateSheetDialog = BottomSheetDialog(context!!)
        val view: View = layoutInflater.inflate(R.layout.gate_popup, null)
        gateSpinner = view.findViewById(R.id.gate_spinner)

        getGatesApi()

        view.findViewById<View>(R.id.gate_submit_btn)
            .setOnClickListener {
                gateSheetDialog!!.dismiss()
                if (AppUtil.isInternetThere(context!!)) {
                    associateActionApi(asc, action)
                    //

                } else {
                    showToast("No Internet!")
                }
            }

        gateSheetDialog!!.setContentView(view)
        gateSheetDialog!!.show()
    }

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

    override fun onResume() {
        super.onResume()

        activity!!.supportActionBar!!.title = "Associates"

        if (isForm == true) {
            reqFormActivity = activity as VisitReqFormActivity
            reqFormActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            detailsActivity = activity as VMSLogListDetailsActivity
            detailsActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initView() {
        loading = viewa!!.findViewById(R.id.dept_progress)
        associatesRV = viewa!!.findViewById(R.id.departments_rv)
        associatesRV!!.layoutManager = LinearLayoutManager(activity!!)
        associatesRV!!.setHasFixedSize(true)

        val adapter = AssociatesListAdapter(this, associatesList, isForm!!)
        associatesRV!!.adapter = adapter
    }

    private fun onLoad() {
        associatesRV!!.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        associatesRV!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        if (activity != null) {

            if (isForm == true) {
                activity!!.supportActionBar!!.title = "Add Visitor"
            } else {
                activity!!.supportActionBar!!.title = "Details"
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRemove(pos: Int) {
        reqFormActivity!!.removeAssociate(pos)
    }

    private fun associateActionApi(asc: AscRecord, action: Int) {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )

        val prefUtil = PrefUtil(activity!!)
        val url = "${PrefUtil.getBaseUrl()}VisitorListing/UpdateSingleStatus"

        val postData = AssociateStatusPost()
        postData.requestID = reqID
        postData.status = action
        postData.gateCode = gateSel
        postData.visitorID = asc.ascVisitorID

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
                                activity!!.supportFragmentManager.popBackStack()
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

    override fun onTokenRefresh(responseCode: Int, token: String) {
        afterLoad()
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(activity!!)
            }
            200 -> {
                showToast("Try Again!")
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == gateSpinner) {
            gateSel = gatesList[position].code!!
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onAscActionClick(asc: AscRecord, action: Int) {
        showGatePickerPopUp(action, asc)
    }
}
