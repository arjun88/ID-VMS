package com.idbsoftek.vms.setup.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.idbsoftek.vms.R

class VMSDashboardGridAdapter(
    private var dashboardMenu: List<DashboardMenu>,
    private var itemClickable: DashboardItemClickable
) :
    RecyclerView.Adapter<VMSDashboardGridAdapter.DashboardGridHolder>() {

    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardGridHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.dashboard_item, parent, false)
        return DashboardGridHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return dashboardMenu.size
    }

    override fun onBindViewHolder(holder: DashboardGridHolder, position: Int) {
        holder.titleTV!!.text = dashboardMenu[position].title
        holder.image!!.setImageResource(
            dashboardMenu[position].image!!
        )

        holder.countTv!!.text = dashboardMenu[position].count.toString()

        if (dashboardMenu[position].count == 0) {
            holder.countTv!!.visibility = View.GONE
        } else {
            holder.countTv!!.visibility = View.VISIBLE
            // animateView(holder)
        }

        holder.itemCV!!.setOnClickListener {

            itemClickable.onClickItem(position)

        }

    }

    class DashboardGridHolder(itemView: View) : ViewHolder(itemView) {
        var titleTV: AppCompatTextView? = null
        var countTv: AppCompatTextView? = null
        var image: AppCompatImageView? = null
        var itemCV: MaterialCardView? = null

        init {
            titleTV = itemView.findViewById(R.id.dashboard_item_text)
            countTv = itemView.findViewById(R.id.count_tv)
            image = itemView.findViewById(R.id.dashboard_item_iv)
            itemCV = itemView.findViewById(R.id.dashboard_cv)
        }
    }
}



