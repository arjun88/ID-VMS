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
import com.idbsoftek.vms.R

class AdminVisitorStatListAdapter(

) :
    RecyclerView.Adapter<AdminVisitorStatListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var deptList: List<VisitorDept> = ArrayList()

    constructor(deptS: List<VisitorDept>) : this() {
        this.deptList = deptS
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
    private fun bindViews(holder: VisitorLogHolder, deptVisitor: VisitorDept) {
        val deptName = deptVisitor.deptName

        holder.deptNameTV!!.text = deptName


        setVisitorStatusRV(
            holder.statusRV!!, deptVisitor.statsList!!
        )

        //holder.countTV!!.text = deptVisitor.numOfVisitors
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {

        var itemCV: MaterialCardView? = null
        var deptNameTV: AppCompatTextView? = null
        var statusRV: RecyclerView? = null
        var contextHolder: Context? = null

        fun setContext(con: Context) {
            this.contextHolder = con
        }

        init {
            itemCV = itemView.findViewById(R.id.dept_cv)

            deptNameTV = itemView.findViewById(R.id.dep_name_admin_tv)
            statusRV = itemView.findViewById(R.id.status_count_rv)

            statusRV!!.layoutManager = GridLayoutManager(contextHolder, 2)
            statusRV!!.setHasFixedSize(true)
        }


    }
}



