package com.idbsoftek.vms.setup.visitor_stats

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VmsMainActivity
import com.idbsoftek.vms.setup.analytics.DepartmentWiseAnalyticsFragment
import com.idbsoftek.vms.setup.analytics.DeptItemClickable
import com.idbsoftek.vms.setup.analytics.DeptListAdapter
import com.idbsoftek.vms.setup.api.DeptList
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.CalendarUtils
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VisitorStatsActivity : VmsMainActivity(), TokenRefreshable, DeptItemClickable {
    private var isApprover: Boolean? = null
    private var activity: VisitorStatsActivity? = null
    private var prefUtil: PrefUtil? = null

    private var statsRV: RecyclerView? = null
    private var statsLoading: ProgressBar? = null
    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    private var visitorStatList: ArrayList<DashboardListItem> = ArrayList()
    private var adminVisitorStatList: ArrayList<DashboardListItem> = ArrayList()
    private var fromDate = ""
    private var toDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visitor_stats)

        activity = this
        prefUtil = PrefUtil(activity!!)

        isApprover = PrefUtil.getVmsEmpROle() == "approver"
        setActionBarTitle("Visitor Stats")

        var todayDate = CalendarUtils.getTodayDate()
        todayDate = CalendarUtils.getDateInRequestedFormat(
            "MM-dd-yyyy",
            "yyyy-MM-dd",
            todayDate
        )
        fromDate = todayDate
        toDate = todayDate

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        statsLoading = findViewById(R.id.stats_loading)
        statsRV = findViewById(R.id.stats_rv)
        statsRV!!.layoutManager = LinearLayoutManager(activity!!)
        statsRV!!.setHasFixedSize(true)

        setStats()
    }

    override fun onStart() {
        super.onStart()
      /*  if (isApprover!!)
            getVisitorStatsApi()
        else*/
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
            val adapter = DeptListAdapter(this, visitorStatList)
            statsRV!!.adapter = adapter
        }
    }

    private fun getVisitorStatsApi() {
        /*  onLoad()
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
              })*/
    }

    private fun getVisitorStatsAdminApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val url = "${PrefUtil.getBaseUrl()}Stats/DepartmentWiseVisitCountApi"

        val prefUtil = PrefUtil(this)
        tokenRefreshSel = this
        apiCallable.loadVisitorStatsAdmin(
            url, prefUtil.getApiToken(),
            fromDate, toDate
        )
            .enqueue(object : Callback<DbVisitorStatsApiResponse> {
                override fun onResponse(
                    call: Call<DbVisitorStatsApiResponse>,
                    response: Response<DbVisitorStatsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                adminVisitorStatList.clear()
                                adminVisitorStatList.addAll(visitorLogApiResponse.dashboardList!!)
                                visitorStatList.clear()
                                visitorStatList.addAll(visitorLogApiResponse.dashboardList)
                                setStats()
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
//                if (isApprover!!)
//                    getVisitorStatsApi()
//                else
                    getVisitorStatsAdminApi()
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
    }

    private fun moveToFragment(isDepartments: Boolean, deptCode: String, fromDate: String, toDate: String) {
        val fragment =
            DepartmentWiseAnalyticsFragment()

        val arg = Bundle()
        arg.putBoolean("IS_DEPT_VIEW", isDepartments)
        arg.putBoolean("FROM_STATS", true)
        arg.putString("DEPT_CODE", deptCode)
        arg.putString("FROM_DATE", fromDate)
        arg.putString("TO_DATE", toDate)
        fragment.arguments = arg

        val fm = supportFragmentManager
        val fT = fm.beginTransaction()
        fT.replace(R.id.stats_view, fragment)
        fT.addToBackStack(null)
        fT.commit()
    }

    override fun onDeptClick(id: String) {

        // analyticsActivity!!.moveToFragment(false)
        moveToFragment(
            false, id, fromDate = fromDate,
            toDate = toDate
        )
    }
}
