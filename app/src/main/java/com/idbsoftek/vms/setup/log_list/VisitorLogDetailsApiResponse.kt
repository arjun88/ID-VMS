package com.idbsoftek.vms.setup.log_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorLogDetailsApiResponse(
    @Expose
    @field:SerializedName("status")
    var status: Boolean? = null,

    @Expose
    @field:SerializedName("message")
    var message: String? = null,

    @Expose
    @field:SerializedName("visitorList")
    var logDetails: VisitorListItem? = null,
)