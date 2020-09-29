package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisRefNumListApiResponse(
    @Expose
    @field:SerializedName("message")
    val message: String? = null,

    @Expose
    @field:SerializedName("status")
    val status: Boolean? = null,

    @Expose
    @field:SerializedName("visitor_list")
    val visitors: List<EmpListItem>? = null
)