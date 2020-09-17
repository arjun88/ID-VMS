package com.idbsoftek.vms.setup.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.api.EmpListItem

class EmpListItemAdapter() :
    RecyclerView.Adapter<EmpListItemAdapter.EmpListHolder>() {
    private var empList: List<EmpListItem>? = null
    private var empSelectable: EmpSelectable? = null
    private var searchItemClickable: SearchItemClickable? = null
    private var isRefNumList: Boolean? = false

    private var isFrom = 0

    private var visitorList: List<SearchResultsItem>? = null

    // 0 - Entry Pass
    // 1 - Search
    // 2 - Ref Num

    constructor(
        visitorList: List<SearchResultsItem>,
        searchItemClickable: SearchItemClickable,
        isFrom: Int?
    ) : this() {
        this.visitorList = visitorList
        this.searchItemClickable = searchItemClickable
        this.isFrom = isFrom!!
    }

    constructor(
        empList: List<EmpListItem>,
        empSelectable: EmpSelectable, isRefNumList: Boolean,
        isFrom: Int?
    ) : this() {
        this.empList = empList
        this.empSelectable = empSelectable
        this.isRefNumList = isRefNumList
        this.isFrom = isFrom!!
    }

    override fun onBindViewHolder(holder: EmpListHolder, position: Int) {
        // val empCodeName = "${empList!![position].code} - ${empList!![position].name}"

        if (isFrom == 1) {
            holder.empTV.text = visitorList!![position].visitorName

            holder.view.setOnClickListener {
                searchItemClickable!!.onSearchItemClick(visitorList!![position])
                clearSelection()
                visitorList!![position].selected = true
            }

            if (visitorList!![position].selected == true) {
                holder.empTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done, 0)
            } else {
                holder.empTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        } else {
            if (isRefNumList!!)
                holder.empTV.text = "${empList!![position].refNum} - ${empList!![position].name}"
            else
                holder.empTV.text = empList!![position].employeeFullName

            holder.view.setOnClickListener {
                empSelectable!!.onEmpSelection(empList!![position])
                clearSelection()
                empList!![position].selected = true
            }

            if (empList!![position].selected == true) {
                holder.empTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done, 0)
            } else {
                holder.empTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    private fun clearSelection() {
        if (isFrom == 1) {
            for (visitor in visitorList!!) {
                visitor.selected = false
            }
        } else {
            for (emp in empList!!) {
                emp.selected = false
            }
        }
        notifyDataSetChanged()
    }

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpListHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.emp_list_item,
            parent, false
        )
        context = parent.context
        return EmpListHolder(view)
    }

    override fun getItemCount(): Int {
        return if (isFrom == 1)
            visitorList!!.size
        else
            empList!!.size
    }

    inner class EmpListHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val empTV: AppCompatTextView
        val view: LinearLayout

        init {
            empTV = itemView.findViewById(R.id.emp_id_name_tv)
            view = itemView.findViewById(R.id.emp_id_name_item)
        }
    }
}
