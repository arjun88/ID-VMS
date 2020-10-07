package com.idbsoftek.vms.setup.form

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.log_list.AscRecord
import com.idbsoftek.vms.util.PrefUtil
import de.hdodenhof.circleimageview.CircleImageView

class AssociatesListAdapter(
    associatesActionable: AssociatesActionable,
    associatesList: List<AscRecord>, isForm: Boolean
) :
    RecyclerView.Adapter<AssociatesListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var associatesList: List<AscRecord> = ArrayList()
    private var isForm: Boolean? = false
    private var associatesActionable: AssociatesActionable? = null

    init {
        this.associatesActionable = associatesActionable
        this.associatesList = associatesList
        this.isForm = isForm
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val pref = PrefUtil(this.context!!)
        val view = LayoutInflater.from(
            parent.context
        ).inflate(
            R.layout
                .associate_list_item, parent, false
        )
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

        holder.nameTV!!.text = visitorLog.ascVisitorName
        holder.mobTV!!.text = "Mob: ${visitorLog.ascVisitorMobile}"
        holder.idTV!!.text = "ID Proof: ${visitorLog.ascProofDetails}"

        holder.removeBtn!!.visibility = View.GONE
        holder.allowBtn!!.visibility = View.GONE
        holder.sessionOutBtn!!.visibility = View.GONE

       // val url = "${visitorLog.ascVisitorID}.png"

        val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${visitorLog.imageName!!}"

       // val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${url}"

        Glide
            .with(context!!)
            .load(fullUrl)
            .placeholder(R.drawable.account)
            .dontAnimate()
            //.apply(RequestOptions.placeholderOf(R.drawable.account).error(R.drawable.account))
            .into(holder.ascIV!!)

        /*if(isForm == false){
            holder.removeBtn!!.visibility = View.GONE
        }

        holder.removeBtn!!.setOnClickListener {
            associateRemovable.onRemove(position)
            notifyDataSetChanged()
        }*/
        if(PrefUtil.getVmsEmpROle() == "security" || PrefUtil.getVmsEmpROle() == "approver")
        showBtnBasedOnStatus(holder, asc = visitorLog)

        holder.allowBtn!!.setOnClickListener {
            val ascI = associatesList[position]
            if(PrefUtil.getVmsEmpROle() == "security") {
                when (ascI.movementStatus) {

                    VMSUtil.ApproveAction -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.CheckInAction)
                    }
                    VMSUtil.MultiDayCheckIn -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.CheckInAction)
                    }

                    VMSUtil.MeetCompleteAction -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.CheckOutAction)
                    }
                }
            }else{
                when (ascI.movementStatus) {

                    VMSUtil.CheckInAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetStartAction)
                    }
                    VMSUtil.MeetStartAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                    VMSUtil.SessionInAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                    VMSUtil.SessionOutAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                }
            }
        }

        holder.sessionOutBtn!!.setOnClickListener {
            val ascI = associatesList[position]
            if(PrefUtil.getVmsEmpROle() == "security") {
                when (ascI.visitorStatus) {
                    VMSUtil.SessionOutAction -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.SessionInAction)
                    }
                    VMSUtil.SessionInAction -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.SessionOutAction)
                    }
                    VMSUtil.MeetStartAction -> {
                        associatesActionable!!.onAscActionClick(ascI, VMSUtil.SessionOutAction)
                    }
                }
            }
            else{
                when(ascI.visitorStatus){
                    VMSUtil.CheckInAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetStartAction)
                    }
                    VMSUtil.MeetStartAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                    VMSUtil.SessionInAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                    VMSUtil.SessionOutAction -> {
                        associatesActionable!!.onAscActionClick(ascI,VMSUtil.MeetCompleteAction)
                    }
                }
            }

        }
    }

    private fun showBtnBasedOnStatus(holder: VisitorLogHolder, asc: AscRecord) {
        when (asc.visitorStatus) {
            VMSUtil.ApproveAction -> {
                if(PrefUtil.getVmsEmpROle() == "security") {
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Allow"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
                else{
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Approved"
                }
            }

            VMSUtil.RejectAction -> {

                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Rejected"

            }
            VMSUtil.CheckInAction -> {

                if(PrefUtil.getVmsEmpROle() == "security"){
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Checked In"
                }
                else{
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Meet Start"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
            }

            VMSUtil.MultiDayCheckIn -> {
                if(PrefUtil.getVmsEmpROle() == "security") {
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Allow"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
                else{
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Muti Day Check In"
                }
            }
            VMSUtil.MeetCompleteAction -> {
                if(PrefUtil.getVmsEmpROle() == "security") {
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Exit"

                    holder.sessionOutBtn!!.visibility = View.GONE
                    holder.sessionOutBtn!!.text = "Session Out"
                }
                else{
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Meet Completed"
                }
            }
            VMSUtil.SessionInAction -> {
                if(PrefUtil.getVmsEmpROle() == "security"){
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Exit"

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Session Out"
                }
                else{
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Meet Complete"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
            }
            VMSUtil.MeetStartAction -> {

                if(PrefUtil.getVmsEmpROle() == "security"){
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Session Out"
                }
                else{
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Meet Complete"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
            }
            VMSUtil.SessionOutAction -> {

                if(PrefUtil.getVmsEmpROle() == "security"){
                    holder.allowBtn!!.visibility = View.GONE

                    holder.sessionOutBtn!!.visibility = View.VISIBLE
                    holder.sessionOutBtn!!.text = "Session In"
                }
                else{
                    holder.allowBtn!!.visibility = View.VISIBLE
                    holder.allowBtn!!.text = "Meet Complete"

                    holder.sessionOutBtn!!.visibility = View.GONE
                }
            }
            else -> {
                holder.allowBtn!!.visibility = View.GONE
                holder.sessionOutBtn!!.visibility = View.GONE
            }
        }
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {
        var nameTV: AppCompatTextView? = null
        var mobTV: AppCompatTextView? = null
        var removeBtn: MaterialButton? = null
        var allowBtn: MaterialButton? = null
        var sessionOutBtn: MaterialButton? = null
        var idTV: AppCompatTextView? = null
        var ascIV: CircleImageView? = null

        init {
            nameTV = itemView.findViewById(R.id.assc_name_tv_item)
            mobTV = itemView.findViewById(R.id.ass_mob_tv_list_item)
            idTV = itemView.findViewById(R.id.ass_v_id_tv)

            ascIV = itemView.findViewById(R.id.ass_iv_list_item)

            removeBtn = itemView.findViewById(R.id.associate_remove_btn)
            allowBtn = itemView.findViewById(R.id.allow_btn_asc_item)
            sessionOutBtn = itemView.findViewById(R.id.session_out_btn_asc_item)
        }
    }
}



