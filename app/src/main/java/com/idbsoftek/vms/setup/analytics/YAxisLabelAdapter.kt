package com.idbsoftek.vms.setup.analytics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.idbsoftek.vms.R

class YAxisLabelAdapter :
    RecyclerView.Adapter<YAxisLabelAdapter.VisitorLogHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.bar_chart_y_axis_item, parent, false)
        return VisitorLogHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {
//        holder.titleTV!!.text = dashboardMenu[position].title
//        holder.image!!.setImageResource(
//            dashboardMenu[position].image!!
//        )


    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {
        //var titleTV: AppCompatTextView? = null
        var countTv: AppCompatTextView? = null

        init {
//            titleTV = itemView.findViewById(R.id.dashboard_item_text)
//            countTv = itemView.findViewById(R.id.count_tv)
//            image = itemView.findViewById(R.id.dashboard_item_iv)
//            itemCV = itemView.findViewById(R.id.visitor_log_list_cv)
//
//            toMeetTV = itemView.findViewById(R.id.to_meet_tv)
//            statusTV = itemView.findViewById(R.id.vms_status_tv)
//            fromTV = itemView.findViewById(R.id.visitor_from_tv_list_item)
//
//            approveBtn = itemView.findViewById(R.id.vms_approve_btn)
//            rejectBtn = itemView.findViewById(R.id.vms_reject_btn)
//            completedBtn = itemView.findViewById(R.id.vms_completed_btn)
//            allowBtn = itemView.findViewById(R.id.vms_allow_btn)
//            exitBtn = itemView.findViewById(R.id.vms_exit_btn)
        }
    }
}



