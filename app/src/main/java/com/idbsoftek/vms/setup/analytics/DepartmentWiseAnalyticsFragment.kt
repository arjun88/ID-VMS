package com.idbsoftek.vms.setup.analytics

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VmsApiClient
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
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentWiseAnalyticsFragment : Fragment(),
    DeptItemClickable,
    VisitorLogItemClickable, TokenRefreshable {
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
        if(fromStats!!){
            if (isDeptView) {
                activity!!.supportActionBar!!.title = "Departments Analytics"
               // statsActivity!!.filterBtn!!.visibility = View.VISIBLE

            } else {
                activity!!.supportActionBar!!.title = "Visitors In Departments"
               // analyticsActivity!!.filterBtn!!.visibility = View.GONE
            }
    }
    else
    {
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

        if(!fromStats!!) {
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
            visitorLogList = visitorsList
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
}
