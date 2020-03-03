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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.idbsoftek.vms.setup.api.AssociatesItem
import com.idbsoftek.vms.setup.log_list.VMSLogListDetailsActivity
import com.idbsoftek.vms.R

class AssociatesListFragment : Fragment(), AssociatesRemovable {
    private var activity: AppCompatActivity? = null
    private var reqFormActivity: VisitReqFormActivity? = null
    private var detailsActivity: VMSLogListDetailsActivity? = null
    private var associatesRV: RecyclerView? = null
    private var viewa: View? = null
    private var loading: ProgressBar? = null
    private var isForm: Boolean? = false

    private var associatesList: List<AssociatesItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewa = inflater.inflate(R.layout.fragment_department_wise_analytics, container, false)

        val arg = arguments

        isForm = arg!!.getBoolean("IS_FORM")
        associatesList = arg.getParcelableArrayList("ASSOCIATES")!!

        val gson = Gson()
        val associatesGson = gson.toJson(associatesList)

        Log.e("ASSOCIATES: ", "$associatesGson")

        initView()
        return viewa
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as AppCompatActivity?

        activity!!.supportActionBar!!.title = "Associates"
    }

    override fun onResume() {
        super.onResume()

        activity!!.supportActionBar!!.title = "Associates"

        if(isForm == true) {
            reqFormActivity = activity as VisitReqFormActivity
            reqFormActivity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        else {
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
}
