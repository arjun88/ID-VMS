package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.AssociatesItem

data class VisitorFormSubmitData(

    @Expose
    @field:SerializedName("empID")
    var empID: String? = null,

    @Expose
    @field:SerializedName("mob")
    var mob: String? = null,

    @Expose
    @field:SerializedName("visitor_photo")
    var visitorPhoto: String? = null,

    @Expose
    @field:SerializedName("from_date")
    var fromDate: String? = null,

    @Expose
    @field:SerializedName("visitor_id_num")
    var visitorIdNum: String? = null,

    @Expose
    @field:SerializedName("associates")
    var associates: List<AssociatesItem?>? = null,

    @Expose
    @field:SerializedName("id_proof")
    var idProof: String? = null,

    @Expose
    @field:SerializedName("assets_num")
    var assetsNum: String? = null,

    @Expose
    @field:SerializedName("from_time")
    var fromTime: String? = null,

    @Expose
    @field:SerializedName("visitor_name")
    var visitorName: String? = null,

    @Expose
    @field:SerializedName("visitor_company")
    var visitorCompany: String? = null,

    @Expose
    @field:SerializedName("visitor_category")
    var visitorCategory: String? = null,

    @Expose
    @field:SerializedName("assets")
    var assets: String? = null,

    @Expose
    @field:SerializedName("to_date")
    var toDate: String? = null,

    @Expose
    @field:SerializedName("appointment_with")
    var appointmentWith: String? = null,

    @Expose
    @field:SerializedName("id_num")
    var idNum: String? = null,

    @Expose
    @field:SerializedName("veh_num")
    var vehNum: String? = null,

    @Expose
    @field:SerializedName("visitor_purpose")
    var visitorPurpose: String? = null,

    @Expose
    @field:SerializedName("to_time")
    var toTime: String? = null
)