package com.idbsoftek.vms.setup.form

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.button.MaterialButton
import com.idbsoftek.vms.setup.api.AssociatesItem
import com.idbsoftek.vms.R

class AssociatesListAdapter(
    private var associateRemovable: AssociatesRemovable,
    associatesList: List<AssociatesItem>, isForm: Boolean
) :
    RecyclerView.Adapter<AssociatesListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var associatesList: List<AssociatesItem> = ArrayList()
    private var isForm: Boolean? = false

    init {
        this.associatesList = associatesList
        this.isForm = isForm
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(
            R.layout
            .associate_list_item, parent, false)
        return VisitorLogHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return associatesList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {
        val visitorLog = associatesList[position]

        holder.nameTV!!.text = visitorLog.name
        holder.mobTV!!.text = "Mob: ${visitorLog.mob}"
        holder.idTV!!.text = "ID Proof: ${visitorLog.idProof}"

        if(isForm == false){
            holder.removeBtn!!.visibility = View.GONE
        }

        holder.removeBtn!!.setOnClickListener {
            associateRemovable.onRemove(position)
            notifyDataSetChanged()
        }
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {
        var nameTV: AppCompatTextView? = null
        var mobTV: AppCompatTextView? = null
        var removeBtn: MaterialButton? = null
        var idTV: AppCompatTextView? = null

        init {
            nameTV = itemView.findViewById(R.id.assc_name_tv_item)
            mobTV = itemView.findViewById(R.id.ass_mob_tv_list_item)
            idTV = itemView.findViewById(R.id.ass_v_id_tv)

            removeBtn = itemView.findViewById(R.id.associate_remove_btn)
        }
    }
}



