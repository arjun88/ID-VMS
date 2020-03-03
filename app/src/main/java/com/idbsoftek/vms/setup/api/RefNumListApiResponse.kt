package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RefNumListApiResponse(
    @Expose
    @field:SerializedName("message")
    val message: String? = null,

    @Expose
    @field:SerializedName("status")
    val status: Boolean? = null,

    @Expose
    @field:SerializedName("visitor_list")
    val visitorList: List<VisitorListItem?>? = null
)