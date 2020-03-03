package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.VisitorLogList

class VisitorLogApiResponse {
    @Expose
    @SerializedName("message")
    var message: String? = null
    @Expose
    @SerializedName("status")
    var status: Boolean? = null
    @Expose
    @SerializedName("visitor_log_list")
    var visitorLogList: List<VisitorLogList>? = null
    @Expose
    @SerializedName("approver_filter_list")
    var filterListFromApprover: List<VisitorLogList>? = null

    @Expose
    @SerializedName("visitorInDepartments")
    var visitorInDept: List<VisitorLogList>? = null

}