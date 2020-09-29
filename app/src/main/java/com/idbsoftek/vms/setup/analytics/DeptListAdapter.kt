package com.idbsoftek.vms.setup.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.visitor_stats.DashboardListItem

class DeptListAdapter(
    private var itemClickable: DeptItemClickable,
    deptS: List<DashboardListItem>
) :
    RecyclerView.Adapter<DeptListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var deptList: List<DashboardListItem> = ArrayList()

    init {
        this.deptList = deptS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.dept_list_item, parent, false)
        return VisitorLogHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return deptList.size
    }

    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {

        holder.itemCV!!.setOnClickListener {
            itemClickable.onDeptClick(deptList[position].deptCode!!)
        }

        val visitorDept = deptList[position]

        bindViews(holder, visitorDept)
    }

    @SuppressLint("DefaultLocale")
    private fun bindViews(holder: VisitorLogHolder, deptVisitor: DashboardListItem) {
        val deptName = deptVisitor.departmentName
        /*if (deptName!!.length > 2) {
            holder.deptNameTV!!.text = deptName.toLowerCase().capitalize()
        } else {
            holder.deptNameTV!!.text = deptName
        }*/

        holder.deptNameTV!!.text = deptName

        val totalCount = deptVisitor.pending!! + deptVisitor.approved!! + deptVisitor.checkIn!!
        + deptVisitor.checkOut!! + deptVisitor.meetInProgress!!
        + deptVisitor.meetCompleted!! + deptVisitor.overStayed!!

            holder.countTV!!.text = "${deptVisitor.totalCount}"
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {

        var itemCV: MaterialCardView? = null
        var deptNameTV: AppCompatTextView? = null
        var countTV: AppCompatTextView? = null

        init {
            itemCV = itemView.findViewById(R.id.dept_cv)

            deptNameTV = itemView.findViewById(R.id.dept_name_item_tv)
            countTV = itemView.findViewById(R.id.dept_count_item_tv)
        }
    }
}



