package com.idbsoftek.vms.setup.visitor_stats

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

class VisitorStatListAdapter(

) :
    RecyclerView.Adapter<VisitorStatListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var deptList: List<DashboardListItem> = ArrayList()

    constructor(deptS: List<DashboardListItem>) : this() {
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

        val visitorDept = deptList[position]

        bindViews(holder, visitorDept)
    }

    @SuppressLint("DefaultLocale")
    private fun bindViews(holder: VisitorLogHolder, deptVisitor: DashboardListItem) {
        val deptName = deptVisitor.departmentName

        holder.deptNameTV!!.text = deptName

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



