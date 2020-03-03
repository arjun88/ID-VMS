package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.VisitorLogList

data class VisitorsInDeptApiResponse(

    @Expose
    @field:SerializedName("visitorInDepartments")
    var visitorInDepartments: List<VisitorLogList>? = null,

    @Expose
    @field:SerializedName("total_num_of_visitors")
    var numOfVisitors: String? = null,

    @Expose
    @field:SerializedName("message")
    var message: String? = null,

    @Expose
    @field:SerializedName("status")
    var status: Boolean? = null
)