package com.idbsoftek.vms.setup.visitor_stats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AdminVisitorStatsApiResponse(

    @Expose
    @field:SerializedName("dashboard_data")
    val statsList: List<VisitorDept>? = null,

    @Expose
    @field:SerializedName("message")
    val message: String? = null,

    @Expose
    @field:SerializedName("status")
    val status: Boolean? = null
)

class VisitorDept {
    @Expose
    @field:SerializedName("count_specific_to_status")
    val statsList: List<StatusListItem>? = null

    @Expose
    @field:SerializedName("dept_name")
    val deptName: String? = null

    @Expose
    @field:SerializedName("dept_code")
    val deptCode: String? = null
}
