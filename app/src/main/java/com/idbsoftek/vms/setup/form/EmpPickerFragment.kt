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
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.DialogUtil
import com.idbsoftek.vms.util.PrefUtil
import com.idbsoftek.vms.setup.api.EmpListItem
import com.idbsoftek.vms.setup.api.ToMeetApiResponse
import com.idbsoftek.vms.setup.api.VMSApiCallable
import com.idbsoftek.vms.setup.api.VmsApiClient
import com.idbsoftek.vms.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmpPickerFragment : Fragment(), EmpSelectable {

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


    private var filteredEmpList: ArrayList<EmpListItem> = ArrayList()
    private var empList: ArrayList<EmpListItem> = ArrayList()

    private var showEmpList: Boolean? = true
    private var otherSubmitBtn: MaterialButton? = null
    private var otherPlaceTxtIP: TextInputLayout? = null

    private var isForPlace: Boolean? = null
    private var isForSrcPlace: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.emp_picker_view, container, false
        )



        dialogUtil = DialogUtil(activity!!)
        prefUtil = PrefUtil(activity!!)

        initView(view)
        return view
    }

    fun empClickInit(empClickSelectable: EmpSelectionClickable) {
        this.empClickSelectable = empClickSelectable
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
                filteredEmpList.clear()
                for (emp in empList) {
                    val empCodeName = emp.name //"${emp.code} - ${emp.name}"
                    if (empCodeName!!.toLowerCase().contains(newText!!.toLowerCase()))
                        filteredEmpList.add(emp)
                }

                when {
                    newText!!.isBlank() -> setEmpList(empList)
                    filteredEmpList.size > 0 -> setEmpList(filteredEmpList)
                    else -> {
                        filteredEmpList.clear()
                        setEmpList(filteredEmpList)

                        dialogUtil!!.showToast("No Employee found based on Search")
                    }
                }
                return true
            }

        })

        empRV = view.findViewById(R.id.emp_list_rv)
        empRV!!.layoutManager = LinearLayoutManager(activity)
        empRV!!.setHasFixedSize(true)

        ViewCompat.isNestedScrollingEnabled(empRV!!)

        if (AppUtil.isInternetThere(activity!!)) {
            getEmployeesList()
        } else
            dialogUtil!!.showToast("No Internet!")

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

    private fun getEmployeesList() {
        onLoad()
        val apiCallable = VmsApiClient.getRetrofit()!!.create(
            VMSApiCallable::class.java
        )
        val prefUtil = PrefUtil(activity!!)
        val url = "${prefUtil.appBaseUrl}EmployeeList"

        apiCallable.getToMeetList(
            url, prefUtil.userName, prefUtil.userName
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
                                setEmpList(empList)

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

                override fun onFailure(call: Call<ToMeetApiResponse>, t: Throwable) {
                    t.printStackTrace()
                    afterLoad()
                }
            })
    }

    private fun setEmpList(empList: List<EmpListItem>) {
        empRV!!.adapter = EmpListItemAdapter(empList, this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as AppCompatActivity?

        setToolbarTitle()
    }

    override fun onDetach() {
        super.onDetach()
        activity!!.supportActionBar!!.title = "Visit Request"
    }

    private fun setToolbarTitle() {
        activity!!.supportActionBar!!.title = "Select Employee"
    }


}