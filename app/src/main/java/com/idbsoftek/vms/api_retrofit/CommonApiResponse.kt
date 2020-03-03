package com.idbsoftek.vms.api_retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonApiResponse(

    @field:SerializedName("session_id")
    @Expose
    val session_id: String? = null,

    @field:SerializedName("message")
    @Expose
    val message: String? = null,

    @field:SerializedName("Message")
    @Expose
    val msg: String? = "",

    @field:SerializedName("app_url")
    @Expose
    val appUrl: String? = null,

    @field:SerializedName("status")
    @Expose
    val status: Boolean? = null,

    @field:SerializedName("attendance")
    @Expose
    val attendance: Boolean? = null,

    @field:SerializedName("leave")
    @Expose
    val leave: Boolean? = null,

    @field:SerializedName("payroll")
    @Expose
    val payroll: Boolean? = null,

    @field:SerializedName("vms")
    @Expose
    val vms: Boolean? = null,

    @field:SerializedName("mobileusers_count")
    @Expose
    val mobUsersCount: Int? = 0,

    @field:SerializedName("type_of_user")
    @Expose
    val userType: String? = "",

    @field:SerializedName("security")
    @Expose
    val security: Boolean? = null,

    @field:SerializedName("Is_approver")
    @Expose
    val isApprover: Boolean? = null,


    @field:SerializedName("analytics")
    @Expose
    val analytics: Boolean? = null,

    @field:SerializedName("admin")
    @Expose
    val admin: Boolean? = null,

    @field:SerializedName("empID")
    @Expose
    val empID: String? = null

)