package com.idbsoftek.vms.setup.form

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.api.*
import com.idbsoftek.vms.setup.login.TokenRefresh
import com.idbsoftek.vms.setup.login.TokenRefreshable
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.DialogUtil
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class EmpPickerFragment : Fragment(), EmpSelectable, SearchItemClickable, TokenRefreshable {
    override fun onEmpSelection(emp: EmpListItem) {
        empClickSelectable!!.onEmpSelectionClick(emp)
        activity!!.supportFragmentManager.popBackStack()
    }

    private var activity: AppCompatActivity? = null
    private var dialogUtil: DialogUtil? = null
    private var prefUtil: PrefUtil? = null
    private var searchView: SearchView? = null
    private var empRV: RecyclerView? = null

    private var loading: ProgressBar? = null
    private var afterLoadView: NestedScrollView? = null
    private var empClickSelectable: EmpSelectionClickable? = null
    private var searchItemClickable: SearchItemClickable? = null


    private var filteredEmpList: ArrayList<EmpListItem> = ArrayList()
    private var empList: ArrayList<EmpListItem> = ArrayList()

    private var searchList: ArrayList<SearchResultsItem> = ArrayList()

    private var showEmpList: Boolean? = true
    private var otherSubmitBtn: MaterialButton? = null
    private var otherPlaceTxtIP: TextInputLayout? = null

    private var isForPlace: Boolean? = null
    private var isForSrcPlace: Boolean? = null
    private var isForRefNum: Boolean? = false

    private var fromScreen = 0

    private var tokenRefreshSel: TokenRefreshable? = null
    private var tokenRefresh: TokenRefresh? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.emp_picker_view, container, false
        )

        isForRefNum = arguments!!.getBoolean("IS_FOR_REF_NUM")
        fromScreen = arguments!!.getInt("FROM_SCREEN")
        setToolbarTitle()

        tokenRefreshSel = this
        tokenRefresh = TokenRefresh().getTokenRefreshInstance(tokenRefreshSel)

        dialogUtil = DialogUtil(activity!!)
        prefUtil = PrefUtil(activity!!)

        initView(view)
        return view
    }

    fun empClickInit(
        empClickSelectable: EmpSelectionClickable,
        searchItemClickable: SearchItemClickable
    ) {
        this.empClickSelectable = empClickSelectable
        this.searchItemClickable = searchItemClickable
        showEmpList = true
    }

    private fun showOtherView() {
        otherPlaceTxtIP!!.visibility = View.VISIBLE
        otherSubmitBtn!!.visibility = View.VISIBLE

        //afterLoadView!!.post({ })
        afterLoadView!!.post {
            afterLoadView!!.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun hideOtherView() {
        otherPlaceTxtIP!!.visibility = View.GONE
        otherSubmitBtn!!.visibility = View.GONE
    }

    private fun initView(view: View?) {
        loading = view!!.findViewById<ProgressBar>(R.id.emp_list_progress)
        afterLoadView = view.findViewById(R.id.emp_after_load)
        searchView = view.findViewById(R.id.emp_search_view)

        otherPlaceTxtIP = view.findViewById(R.id.other_pick_up_pt_txt_ip)
        otherSubmitBtn = view.findViewById(R.id.other_sel_btn)

        val bundle = arguments
        //  showEmpList = bundle!!.getBoolean("SHOW_EMP_LIST")
        Log.e("SHOW_EMP_LIST", ": " + showEmpList!!)

        isForPlace = bundle!!.getBoolean("IS_FOR_PLACE")
        isForSrcPlace = bundle.getBoolean("IS_FROM_PICK_UP")

        setToolbarTitle()

        searchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("Search", "Submit")
                //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (fromScreen == 1) {
                    if (newText!!.isNotEmpty()) {
                        getSearchList(newText)
                    }
                } else {
                    filteredEmpList.clear()
                    for (emp in empList) {
                        var empCodeName = emp.name
                        if (isForRefNum!!)
                            empCodeName = "${emp.refNum} - ${emp.name}"
                        //  val empCodeName = emp.name //"${emp.code} - ${emp.name}"
                        if (emp.employeeFullName!!.toLowerCase(Locale.ROOT).contains(
                                newText!!.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        )
                            filteredEmpList.add(emp)
                    }

                    when {
                        newText!!.isBlank() -> setEmpList(empList, isForRefNum!!)
                        filteredEmpList.size > 0 -> setEmpList(filteredEmpList, isForRefNum!!)
                        else -> {
                            filteredEmpList.clear()
                            setEmpList(filteredEmpList, isForRefNum!!)

                            dialogUtil!!.showToast("No Employee found based on Search")
                        }
                    }
                }

                return true
            }

        })

        empRV = view.findViewById(R.id.emp_list_rv)
        empRV!!.layoutManager = LinearLayoutManager(activity)
        empRV!!.setHasFixedSize(true)

        ViewCompat.isNestedScrollingEnabled(empRV!!)
        if (fromScreen != 1) {
            if (AppUtil.isInternetThere(activity!!)) {

                if (!isForRefNum!!)
                    getEmployeesList()
                else
                    getRefNumApi()
            } else
                dialogUtil!!.showToast("No Internet!")
        }

        hideOtherView()
    }

    private fun onLoad() {
        loading!!.visibility = View.VISIBLE
        afterLoadView!!.visibility = View.GONE
    }

    private fun afterLoad() {
        afterLoadView!!.visibility = View.VISIBLE
        loading!!.visibility = View.GONE
    }

    private fun showToast(msg: String) {
        Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
    }

    private fun getSearchList(searchKeyword: String?) {
        // onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url =
            "https://vms.idbssoftware.com/api/VMC/SearchVisitor"//"${prefUtil.appBaseUrl}EmployeeList"

        apiCallable.searchVisitor(
            url, searchKeyword!!
        )
            .enqueue(object : Callback<SearchVisitorApiResponse> {
                override fun onResponse(
                    call: Call<SearchVisitorApiResponse>,
                    response: Response<SearchVisitorApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                searchList.clear()
                                searchList =
                                    response.body()!!.searchResults as ArrayList<SearchResultsItem>
                                setSearchList(searchList)

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

                override fun onFailure(call: Call<SearchVisitorApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }


    private fun getEmployeesList() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url =
            "${PrefUtil.getBaseUrl()}VMC/VMCEmployeeList"//"${prefUtil.appBaseUrl}EmployeeList"
        tokenRefreshSel = this
        apiCallable.getToMeetEmpApi(
            url,
            prefUtil.getApiToken()
        )
            .enqueue(object : Callback<ToMeetApiResponse> {
                override fun onResponse(
                    call: Call<ToMeetApiResponse>,
                    response: Response<ToMeetApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                empList.clear()
                                empList = response.body()!!.empList as ArrayList<EmpListItem>
                                setEmpList(empList, isForRefNum!!)

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
                        }
                        response.code() == 500 -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<ToMeetApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun setEmpList(empList: List<EmpListItem>, isRefNumList: Boolean) {
        empRV!!.adapter = EmpListItemAdapter(empList, this, isRefNumList, fromScreen)
    }

    private fun setSearchList(visitorsList: List<SearchResultsItem>) {
        empRV!!.adapter = EmpListItemAdapter(visitorsList, this, fromScreen)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as AppCompatActivity?

        setToolbarTitle()
    }

    override fun onDetach() {
        super.onDetach()

        // setToolbarTitle()

        activity!!.supportActionBar!!.title = "Visitor Entry Pass"

    }

    private fun setToolbarTitle() {
        if (fromScreen == 1) {
            activity!!.supportActionBar!!.title = "Search Visitor"
        } else {
            if (isForRefNum!!)
                activity!!.supportActionBar!!.title = "Visitor Reference Num"
            else
                activity!!.supportActionBar!!.title = "Visit Request"
        }
    }

    private fun getRefNumApi() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${prefUtil.appBaseUrl}QuickCheckVisitors"


        apiCallable.getRefNumListSearch(
            url, prefUtil.userName, prefUtil.sessionID
        )
            .enqueue(object : Callback<VisRefNumListApiResponse> {
                override fun onResponse(
                    call: Call<VisRefNumListApiResponse>,
                    response: Response<VisRefNumListApiResponse>
                ) {
                    when {
                        response.code() == 200 -> {
                            val visitorLogApiResponse = response.body()
                            if (visitorLogApiResponse!!.status == true) {
                                empList.clear()
                                empList = response.body()!!.visitors as ArrayList<EmpListItem>
                                setEmpList(empList, isForRefNum!!)

                                afterLoad()
                                //afterLoad()
                            } else {
                                //afterLoad()
                                showToast(response.body()!!.message!!)
                                afterLoad()
                            }
                        }
                        response.code() == 500 -> {
                            afterLoad()
                        }
                    }
                }

                override fun onFailure(call: Call<VisRefNumListApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    override fun onSearchItemClick(searchData: SearchResultsItem) {
        searchItemClickable!!.onSearchItemClick(searchData)
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun onTokenRefresh(responseCode: Int, token: String) {
        afterLoad()
        when (responseCode) {
            401 -> {
                AppUtil.onSessionOut(activity!!)
            }
            200 -> {
                getEmployeesList()
            }
            else -> {
                AppUtil.onSessionOut(activity!!)
            }
        }
    }


}