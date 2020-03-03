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
import com.idbsoftek.vms.util.PrefUtil
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.log_list.VisitorLogItemClickable
import com.idbsoftek.vms.setup.log_list.VistorLogListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepartmentWiseAnalyticsFragment : Fragment(),
    DeptItemClickable,
    VisitorLogItemClickable {
    private var activity: AppCompatActivity? = null
    private var analyticsActivity: VmsAnalyticsActivity? = null
    private var deptRV: RecyclerView? = null
    private var viewa: View? = null
    private var isDeptView: Boolean = true
    private var loading: ProgressBar? = null
    private var deptCodeSel: String = ""
    private var deptList: List<DeptList>? = ArrayList()

    private var fromDate: String? = ""
    private var toDate: String? = ""

    private var isFromFilter: Boolean? = false

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

        initView()
        return viewa
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as AppCompatActivity?
        analyticsActivity = activity as VmsAnalyticsActivity

        titleAndViewSetUp()

        analyticsActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        titleAndViewSetUp()
    }

    private fun titleAndViewSetUp() {
        if (isDeptView) {
            activity!!.supportActionBar!!.title = "Departments Analytics"
            analyticsActivity!!.filterBtn!!.visibility = View.VISIBLE
        } else {
            activity!!.supportActionBar!!.title = "Visitors In Departments"
            analyticsActivity!!.filterBtn!!.visibility = View.GONE
        }
    }

    private fun initView() {
        loading = viewa!!.findViewById(R.id.dept_progress)
        deptRV = viewa!!.findViewById(R.id.departments_rv)
        deptRV!!.layoutManager = LinearLayoutManager(activity!!)
        deptRV!!.setHasFixedSize(true)

        analyticsActivity!!.filterBtn!!.setOnClickListener {
            Log.e("Filter", "Pop Up")
            analyticsActivity!!.showFilterPopUp(true)
        }

        if (isDeptView) {
            //setDepartments()
            if (isFromFilter!!) {
                setDepartments(deptList = deptList!!)
            } else
                getDeptAnalyticsApi()
        } else {
//            val visitorLogList = ArrayList<VisitorLogList>()
//            setVisitorsInDept(visitorLogList)

            getVisitorLogListApi()
        }
    }

    private fun setDepartments(deptList: List<DeptList>?) {
        val adapter =
            DeptListAdapter(
                this,
                deptList!!
            )
        deptRV!!.adapter = adapter
    }

    private fun setVisitorsInDept(visitorsList: List<VisitorLogList>) {
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

    override fun onVisitorLogItemClick(id: String) {

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
        val url = "${prefUtil.appBaseUrl}DepartmentSpecificAnalytics"

        apiCallable.loadVisitorsInDeptAnalytics(
            url, prefUtil.userName, prefUtil.userName, deptCodeSel,
            fromDate, toDate
        )
            .enqueue(object : Callback<VisitorLogApiResponse> {
                override fun onResponse(
                    call: Call<VisitorLogApiResponse>,
                    response: Response<VisitorLogApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                setVisitorsInDept(visitorLogApiResponse.visitorInDept!!)
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

                override fun onFailure(call: Call<VisitorLogApiResponse>, t: Throwable) {
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
        val url = "${prefUtil.appBaseUrl}DepartmentAnalyticsList"

        apiCallable.loadDeptAnalytics(
            url, prefUtil.userName, prefUtil.sessionID
        )
            .enqueue(object : Callback<DeptAnalyticsApiResponse> {
                override fun onResponse(
                    call: Call<DeptAnalyticsApiResponse>,
                    response: Response<DeptAnalyticsApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                if (visitorLogApiResponse.visitorInDepartments != null) {
                                    if (visitorLogApiResponse.visitorInDepartments.isNotEmpty()) {
                                        setDepartments(visitorLogApiResponse.visitorInDepartments)
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
                        response.code() == 500 -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<DeptAnalyticsApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }
}
