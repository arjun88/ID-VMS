package com.idbsoftek.vms.setup.visitor_stats

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.idbsoftek.vms.R

class StatListGridAdapter() :
    RecyclerView.Adapter<StatListGridAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var deptList: List<StatusListItem> = ArrayList()

    constructor(deptS: List<StatusListItem>) : this() {
        this.deptList = deptS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.status_count_item, parent, false)
        return VisitorLogHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return deptList.size
    }

    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {
        if (position % 2 != 0) {
            holder.countTV!!.gravity = Gravity.END
        } else
            holder.countTV!!.gravity = Gravity.START
        val visitorDept = deptList[position]

        bindViews(holder, visitorDept)
    }

    @SuppressLint("DefaultLocale")
    private fun bindViews(holder: VisitorLogHolder, deptVisitor: StatusListItem) {

        val statusNameCount = "${deptVisitor.name}: ${deptVisitor.count}"

        holder.countTV!!.text = statusNameCount
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {

        /* var itemCV: MaterialCardView? = null
         var deptNameTV: AppCompatTextView? = null*/
        var countTV: AppCompatTextView? = null

        init {
            /* itemCV = itemView.findViewById(R.id.dept_cv)

             deptNameTV = itemView.findViewById(R.id.dept_name_item_tv)*/
            countTV = itemView.findViewById(R.id.status_count_item_tv)
        }
    }
}



