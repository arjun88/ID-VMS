package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorActionApiResponse(

    @Expose
    @field:SerializedName("next_action")
    val nextAction: String? = null,

    @Expose
    @field:SerializedName("message")
    val message: String? = null,

    @Expose
    @field:SerializedName("status")
    val status: Boolean? = null
)