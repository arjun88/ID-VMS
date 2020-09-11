package com.idbsoftek.vms.setup.visitor_stats

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitorStatsActivity : VmsMainActivity() {
    private var isApprover: Boolean? = null
    private var activity: VisitorStatsActivity? = null
    private var prefUtil: PrefUtil? = null

    private var statsRV: RecyclerView? = null
    private var statsLoading: ProgressBar? = null

    private var visitorStatList: ArrayList<StatusListItem> = ArrayList()
    private var adminVisitorStatList: ArrayList<VisitorDept> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor_stats)

        activity = this
        prefUtil = PrefUtil(activity!!)

        isApprover = PrefUtil.getVmsEmpROle() == "approver"
        setActionBarTitle("Visitor Stats")

        statsLoading = findViewById(R.id.stats_loading)
        statsRV = findViewById(R.id.stats_rv)
        statsRV!!.layoutManager = LinearLayoutManager(activity!!)
        statsRV!!.setHasFixedSize(true)

        setStats()
    }

    override fun onStart() {
        super.onStart()
        if (isApprover!!)
            getVisitorStatsApi()
        else
            getVisitorStatsAdminApi()
    }

    private fun onLoad() {
        findViewById<ProgressBar>(R.id.stats_loading).visibility = View.VISIBLE
    }

    private fun afterLoad() {
        findViewById<ProgressBar>(R.id.stats_loading).visibility = View.GONE
    }

    private fun setStats() {
        if (!isApprover!!) {
            val adapter = AdminVisitorStatListAdapter(adminVisitorStatList)
            statsRV!!.adapter = adapter
        } else {
            val adapter = VisitorStatListAdapter(visitorStatList)
            statsRV!!.adapter = adapter
        }
    }

    private fun getVisitorStatsApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val url = "${prefUtil!!.appBaseUrl}DashEmployee"

        apiCallable.loadVisitorStats(
            url, prefUtil!!.userName, prefUtil!!.sessionID
        )
            .enqueue(object : Callback<VisitorStatsApiResponse> {
                override fun onResponse(
                    call: Call<VisitorStatsApiResponse>,
                    response: Response<VisitorStatsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                visitorStatList.clear()
                                visitorStatList.addAll(visitorLogApiResponse.statsList!!)
                                setStats()
                                afterLoad()
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

                override fun onFailure(call: Call<VisitorStatsApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun getVisitorStatsAdminApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val url = "${prefUtil!!.appBaseUrl}DashAdminSecu"

        apiCallable.loadVisitorStatsAdmin(
            url, prefUtil!!.userName, prefUtil!!.sessionID
        )
            .enqueue(object : Callback<AdminVisitorStatsApiResponse> {
                override fun onResponse(
                    call: Call<AdminVisitorStatsApiResponse>,
                    response: Response<AdminVisitorStatsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                adminVisitorStatList.clear()
                                adminVisitorStatList.addAll(visitorLogApiResponse.statsList!!)
                                setStats()
                                afterLoad()
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

                override fun onFailure(call: Call<AdminVisitorStatsApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }
}
