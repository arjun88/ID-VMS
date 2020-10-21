package com.idbsoftek.vms.setup.visitor_stats

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.idbsoftek.vms.R

class AdminVisitorStatListAdapter(

) :
    RecyclerView.Adapter<AdminVisitorStatListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var deptList: List<DashboardListItem> = ArrayList()
    private var statusList: ArrayList<StatusListItem> = ArrayList()

    constructor(deptS: List<DashboardListItem>) : this() {
        this.deptList = deptS

        for(stat in deptS){
            var statusListItem = StatusListItem()
            statusListItem.name = "Pending"
            statusListItem.count = stat.pending.toString()

            statusList.add(statusListItem)

            statusListItem = StatusListItem()
            statusListItem.name = "Approved"
            statusListItem.count = stat.pending.toString()
            statusList.add(statusListItem)

            statusListItem = StatusListItem()
            statusListItem.name = "Checked In"
            statusListItem.count = stat.checkIn.toString()
            statusList.add(statusListItem)

            statusListItem = StatusListItem()
            statusListItem.name = "Meet In Progress"
            statusListItem.count = stat.meetInProgress.toString()
            statusList.add(statusListItem)

            statusListItem = StatusListItem()
            statusListItem.name = "Meet Completed"
            statusListItem.count = stat.meetCompleted.toString()
            statusList.add(statusListItem)

            statusListItem = StatusListItem()
            statusListItem.name = "Checked Out"
            statusListItem.count = stat.checkOut.toString()
            statusList.add(statusListItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.admin_visitor_stat_item, parent, false)
        val holder = VisitorLogHolder(
            view
        )
        holder.setContext(context!!)
        return holder
    }

    override fun getItemCount(): Int {
        return deptList.size
    }

    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {

        val visitorDept = deptList[position]

        bindViews(holder, visitorDept)
        // bindViews(holder, visitorDept)
    }

    private fun setVisitorStatusRV(statusRV: RecyclerView, statsList: List<StatusListItem>) {
        val adapter = StatListGridAdapter(statsList)
        statusRV.adapter = adapter
    }

    @SuppressLint("DefaultLocale")
    private fun bindViews(holder: VisitorLogHolder, deptVisitor: DashboardListItem) {
        val deptName = deptVisitor.departmentName

        holder.deptNameTV!!.text = deptName
        holder.pendingTV!!.text = "Pending: ${deptVisitor.pending}"
        holder.approvedTV!!.text = "Approved: ${deptVisitor.approved}"
        holder.checkedInTV!!.text = "Checked In: ${deptVisitor.checkIn}"
        holder.checkedOutTV!!.text = "Checked Out: ${deptVisitor.checkOut}"
        holder.meetStartTV!!.text = "Meet In Progress: ${deptVisitor.meetInProgress}"
        holder.meetCompletedTV!!.text = "Meet Completed: ${deptVisitor.meetCompleted}"

        holder.sessionOutTV!!.text = "Session Out: ${deptVisitor.sessionOut}"
        holder.overStayTV!!.text = "Over Stayed: ${deptVisitor.overStayed}"
        holder.checkOutPendingTV!!.text = "Check Out Pending: ${deptVisitor.checkOutPending}"

       /* setVisitorStatusRV(
            holder.statusRV!!, statusList
        )*/

    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {

        var itemCV: MaterialCardView? = null
        var deptNameTV: AppCompatTextView? = null
        var statusRV: RecyclerView? = null
        var contextHolder: Context? = null

        var pendingTV: MaterialTextView? = null
        var checkedInTV: MaterialTextView? = null
        var approvedTV: MaterialTextView? = null
        var checkedOutTV: MaterialTextView? = null
        var meetStartTV: MaterialTextView? = null
        var meetCompletedTV: MaterialTextView? = null
        var sessionOutTV: MaterialTextView? = null
        var overStayTV: MaterialTextView? = null
        var checkOutPendingTV: MaterialTextView? = null

        fun setContext(con: Context) {
            this.contextHolder = con
        }

        init {
            itemCV = itemView.findViewById(R.id.dept_cv)

            deptNameTV = itemView.findViewById(R.id.dep_name_admin_tv)

            pendingTV = itemView.findViewById(R.id.pending_count_tv)
            checkedInTV = itemView.findViewById(R.id.checked_in_count_tv)
            approvedTV = itemView.findViewById(R.id.approved_count_tv)
            checkedOutTV = itemView.findViewById(R.id.checked_out_count_tv)
            meetStartTV = itemView.findViewById(R.id.meet_started_count_tv)
            meetCompletedTV = itemView.findViewById(R.id.meet_completed_count_tv)
            checkOutPendingTV = itemView.findViewById(R.id.check_out_pending_count_tv)

            sessionOutTV = itemView.findViewById(R.id.session_out_count_tv)
            overStayTV = itemView.findViewById(R.id.over_stay_count_tv)

            statusRV = itemView.findViewById(R.id.status_count_rv)

            statusRV!!.layoutManager = GridLayoutManager(contextHolder, 2)
            statusRV!!.setHasFixedSize(true)
        }

    }
}



