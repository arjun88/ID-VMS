package com.idbsoftek.vms.setup.log_list

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.idbsoftek.vms.R
import com.idbsoftek.vms.setup.VMSUtil
import com.idbsoftek.vms.setup.analytics.AdminActionable
import com.idbsoftek.vms.util.PrefUtil
import de.hdodenhof.circleimageview.CircleImageView

class VistorLogListAdapter(
    private var itemClickable: VisitorLogItemClickable,
    isFromAnalytics: Boolean?,
    visitorLogList: List<VisitorListItem>,
    adminActionable: AdminActionable
) :
    RecyclerView.Adapter<VistorLogListAdapter.VisitorLogHolder>() {

    private var context: Context? = null
    private var isFromAnalytics: Boolean? = false
    private var adminActionable: AdminActionable? = null

    //private var isSecurityView: Boolean? = false
    private var visitorLogList: List<VisitorListItem> = ArrayList()

    init {
        this.adminActionable = adminActionable
        this.isFromAnalytics = isFromAnalytics
        this.visitorLogList = visitorLogList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorLogHolder {
        this.context = parent.context
        val view = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.visitor_log_menu_item, parent, false)
        return VisitorLogHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return visitorLogList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: VisitorLogHolder, position: Int) {
        val visitorLog = visitorLogList[position]
        val refNum = "" //visitorLog.refNum

        holder.nameTV!!.text = "${visitorLog.visitorName} - ${visitorLog.categoryName}"
        holder.reasonTV!!.text = "Purpose: ${visitorLog.purposeName}"
        holder.timeTV!!.text = "Company: ${visitorLog.visitorCompany}"
        holder.curStatusTV!!.text = VMSUtil.getStatusToShow(visitorLog.status)

        val url = "${visitorLog.imageName}"
        val fullUrl = "${PrefUtil.getVmsImageBaseUrl()}${url}"

        Glide
            .with(context!!)
            .load(fullUrl)
            .placeholder(R.drawable.account)
            .apply(RequestOptions.placeholderOf(R.drawable.account).error(R.drawable.account))
            .dontAnimate()
            .into(holder.image!!)

        holder.itemCV!!.setOnClickListener {
            itemClickable.onVisitorLogItemClick(
                visitorLog.requestID.toString(), "22-09-2020"
            )
        }

        holder.fromTV!!.text = "Date: ${visitorLog.fromDate} to ${visitorLog.toDate}"

        holder.toMeetTV!!.text = "To Meet: ${visitorLog.employeeFullName}"

        var movStatus = visitorLog.status.toString()
        if (visitorLog.isOverStayed!! && visitorLog.status != VMSUtil.CheckOutAction)
            movStatus = "OS"

        if (isFromAnalytics!!) {
            clearAllActions(holder)

            if (visitorLog.status != VMSUtil.CheckOutAction && PrefUtil.getVmsEmpROle() == "admin") {
                holder.viewBtn!!.text = "Action"
                holder.viewBtn!!.visibility = View.VISIBLE

                holder.itemCV!!.setOnClickListener {
                    if (adminActionable != null) {
                        adminActionable!!.onAdminAction(visitorLog)
                    }
                }
            }
        } else
            showBtnViewBasedOnStatus(status = movStatus, holder = holder)

        holder.approveBtn!!.setOnClickListener {
            this.itemClickable.onVisitorLogAction(
                refNum,
                VMSUtil.APPROVE_ACTION
            )
            notifyDataSetChanged()
        }

        holder.rejectBtn!!.setOnClickListener {
            this.itemClickable.onVisitorLogAction(
                refNum,
                VMSUtil.REJECT_ACTION
            )
            notifyDataSetChanged()
        }

        holder.completedBtn!!.setOnClickListener {
            this.itemClickable.onVisitorLogAction(
                refNum,
                VMSUtil.COMPLETE_ACTION
            )
            notifyDataSetChanged()
        }

        holder.allowBtn!!.setOnClickListener {
            this.itemClickable.onVisitorLogAction(
                refNum, VMSUtil.ALLOW_ACTION
            )
            notifyDataSetChanged()
        }

        holder.exitBtn!!.setOnClickListener {
            this.itemClickable.onVisitorLogAction(
                refNum, VMSUtil.EXIT_ACTION
            )
            notifyDataSetChanged()
        }


    }

    private fun loadImage(image: CircleImageView?, base64String: String) {
        try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            image!!.setImageBitmap(decodedImage)
        } catch (e: Exception) {

        }

    }

    @SuppressLint("WrongConstant")
    private fun manageBlinkEffect(tv: AppCompatTextView) {
        val anim = ObjectAnimator.ofInt(
            tv, "textColor", Color.RED, Color.BLUE,
            Color.RED
        )

        anim.duration = 750
        anim.setEvaluator(ArgbEvaluator())
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.start()
    }

    private fun View.blinkAnim(
        times: Int = Animation.INFINITE,
        duration: Long = 500L,
        offset: Long = 200L,
        minAlpha: Float = 0.0f,
        maxAlpha: Float = 1.0f,
        repeatMode: Int = Animation.REVERSE
    ) {
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }

    private fun overStayAlert(view: View) {
        view.blinkAnim()
    }

    @SuppressLint("DefaultLocale")
    private fun showBtnViewBasedOnStatus(status: String?, holder: VisitorLogHolder) {
//        0 - Default (Approver) 1- Reject 2 - Completed

        if (status == "OS") {
            clearAllActions(holder)
            holder.curStatusTV!!.visibility = View.VISIBLE
            holder.curStatusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
            val redColor =
                ContextCompat.getColor(context!!, R.color.red)
            holder.curStatusTV!!.setTextColor(redColor)
            holder.curStatusTV!!.text =
                "Over Stayed"

            manageBlinkEffect(holder.curStatusTV!!)
            //overStayAlert(holder.curStatusTV!!)

            // holder.curStatusTV!!.blinkAnim()
        } else {
            holder.curStatusTV!!.clearAnimation()
            when {
                PrefUtil.getVmsEmpROle().toLowerCase() == "admin" -> {
                    when (status) {
                        VMSUtil.APPROVE_REJECT_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.approveBtn!!.visibility = View.VISIBLE
                            holder.rejectBtn!!.visibility = View.VISIBLE
                            holder.approveRejectView!!.visibility = View.VISIBLE
                        }
                        VMSUtil.REJECTED -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor = ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                VMSUtil.REJECTED
                            holder.approveRejectView!!.visibility = View.GONE
                        }
                        VMSUtil.ALLOW_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.allowBtn!!.visibility = View.VISIBLE
                        }

                        VMSUtil.ADMIN_COMPLETED_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.completedBtn!!.visibility = View.VISIBLE
                        }
                        VMSUtil.ADMIN_EXIT_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.exitBtn!!.visibility = View.VISIBLE
                        }
                        VMSUtil.EXPIRED -> {
                            Log.e("EXPIRED", "Status")
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text = "Expired"
                        }

                        else -> {
                            clearAllActions(holder)
                        }
                    }
                }
                PrefUtil.getVmsEmpROle() == "security" -> {
                    when (status) {
                        VMSUtil.ALLOW_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.allowBtn!!.visibility = View.VISIBLE
                        }

                        VMSUtil.EXIT_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.exitBtn!!.visibility = View.VISIBLE
                        }

                        "Pending" -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text = "Pending"
                        }

                        VMSUtil.EXPIRED -> {
                            Log.e("EXPIRED", "Status")
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text = "Expired"
                        }

                        VMSUtil.REJECTED -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor = ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                VMSUtil.REJECTED
                            holder.approveRejectView!!.visibility = View.GONE
                        }

                        "CheckIn" -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                            val redColor = ContextCompat.getColor(context!!, R.color.green)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                "Allowed"
                            holder.approveRejectView!!.visibility = View.GONE
                        }

                        "Exit" -> {
                            clearAllActions(holder)
                        }

                        "Future" -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                status
                        }

                        else -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                status
                        }
                    }
                }
                else -> {
                    when (status) {
                        VMSUtil.APPROVE_REJECT_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.approveBtn!!.visibility = View.VISIBLE
                            holder.rejectBtn!!.visibility = View.VISIBLE
                            holder.approveRejectView!!.visibility = View.VISIBLE
                        }
                        VMSUtil.COMPLETED_BTN_ENABLED -> {
                            clearAllActions(holder)
                            holder.completedBtn!!.visibility = View.VISIBLE
                        }
                        "Approved" -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                            val greenColor = ContextCompat.getColor(context!!, R.color.green)
                            holder.statusTV!!.setTextColor(greenColor)
                            holder.statusTV!!.text =
                                "Approved"
                            holder.approveRejectView!!.visibility = View.GONE
                        }
                        VMSUtil.REJECTED -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor = ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                VMSUtil.REJECTED
                            holder.approveRejectView!!.visibility = View.GONE
                        }
                        VMSUtil.COMPLETED -> {
                            clearAllActions(holder)
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_green_bg)
                            val greenColor = ContextCompat.getColor(context!!, R.color.green)
                            holder.statusTV!!.setTextColor(greenColor)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.text =
                                VMSUtil.COMPLETED
                            holder.approveRejectView!!.visibility = View.GONE
                        }
                        VMSUtil.EXPIRED -> {
                            Log.e("EXPIRED", "Status")
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text = "Expired"
                        }

                        "Future" -> {
                            clearAllActions(holder)
                            holder.statusTV!!.visibility = View.VISIBLE
                            holder.statusTV!!.setBackgroundResource(R.drawable.rect_red_bg)
                            val redColor =
                                ContextCompat.getColor(context!!, R.color.red)
                            holder.statusTV!!.setTextColor(redColor)
                            holder.statusTV!!.text =
                                status
                        }

                        else -> {
                            clearAllActions(holder)
                        }
                    }
                }
            }

        }

    }

    private fun clearAllActions(holder: VisitorLogHolder) {
        holder.approveBtn!!.visibility = View.GONE
        holder.rejectBtn!!.visibility = View.GONE
        holder.completedBtn!!.visibility = View.GONE
        holder.exitBtn!!.visibility = View.GONE
        holder.allowBtn!!.visibility = View.GONE
        holder.statusTV!!.visibility = View.GONE
        holder.approveRejectView!!.visibility = View.GONE
    }

    class VisitorLogHolder(itemView: View) : ViewHolder(itemView) {
        var titleTV: AppCompatTextView? = null
        var countTv: AppCompatTextView? = null
        var image: CircleImageView? = null
        var itemCV: MaterialCardView? = null
        var approveBtn: MaterialButton? = null
        var rejectBtn: MaterialButton? = null
        var completedBtn: MaterialButton? = null
        var allowBtn: MaterialButton? = null
        var exitBtn: MaterialButton? = null
        var viewBtn: MaterialButton? = null
        var toMeetTV: AppCompatTextView? = null
        var fromTV: AppCompatTextView? = null
        var statusTV: AppCompatTextView? = null
        var nameTV: AppCompatTextView? = null
        var reasonTV: AppCompatTextView? = null
        var timeTV: AppCompatTextView? = null
        var curStatusTV: AppCompatTextView? = null

        var approveRejectView: View? = null

        init {
//            titleTV = itemView.findViewById(R.id.dashboard_item_text)
//            countTv = itemView.findViewById(R.id.count_tv)
            image = itemView.findViewById(R.id.visitor_img_item)
            itemCV = itemView.findViewById(R.id.visitor_log_list_cv)

            approveRejectView = itemView.findViewById(R.id.approve_rej_view_vms_list)

            toMeetTV = itemView.findViewById(R.id.to_meet_tv)
            statusTV = itemView.findViewById(R.id.vms_status_tv)
            fromTV = itemView.findViewById(R.id.visitor_from_tv_list_item)
            curStatusTV = itemView.findViewById(R.id.cur_status_tv_item)

            nameTV = itemView.findViewById(R.id.visitor_name_tv_item)
            reasonTV = itemView.findViewById(R.id.reason_tv_item)
            timeTV = itemView.findViewById(R.id.timings_tv_item)

            approveBtn = itemView.findViewById(R.id.vms_approve_btn)
            rejectBtn = itemView.findViewById(R.id.vms_reject_btn)
            completedBtn = itemView.findViewById(R.id.vms_completed_btn)
            allowBtn = itemView.findViewById(R.id.vms_allow_btn)
            exitBtn = itemView.findViewById(R.id.vms_exit_btn)
            viewBtn = itemView.findViewById(R.id.view_btn)
        }
    }
}



