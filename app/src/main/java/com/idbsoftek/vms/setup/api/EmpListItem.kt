package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmpListItem(

    @Expose
    @field:SerializedName("code")
    var code: String? = null,

    @Expose
    @field:SerializedName("name")
    var name: String? = null,

    @Expose
    @field:SerializedName("employeeId")
    var employeeId: String? = null,

    @Expose
    @field:SerializedName("employeeFullName")
    var employeeFullName: String? = null,

    @Expose
    @field:SerializedName("employeeMobile")
    var employeeMobile: String? = null,

    @Expose
    @field:SerializedName("employeeEmail")
    var employeeEmail: String? = null,

    @Expose
    @field:SerializedName("departmentCode")
    var departmentCode: String? = null,

    @Expose
    @field:SerializedName("designationCode")
    var designationCode: String? = null,

    @Expose
    @field:SerializedName("visitor_ref_num")
    var refNum: String? = null,

    var selected: Boolean? = null,

    val emplCodeName: String? = "$code - $name"
)