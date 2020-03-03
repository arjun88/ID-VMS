package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.AssociatesItem

class VisitDetail {
    @Expose
    @SerializedName("assets")
    var assets: String? = null
    @Expose
    @SerializedName("company")
    var company: String? = null
    @Expose
    @SerializedName("date")
    var date: String? = null
    @Expose
    @SerializedName("id_number")
    var idNumber: String? = null
    @Expose
    @SerializedName("id_proof")
    var idProof: String? = null
    @Expose
    @SerializedName("name")
    var name: String? = null
    @Expose
    @SerializedName("purpose")
    var purpose: String? = null
    @Expose
    @SerializedName("ref_num")
    var refNum: String? = null
    @Expose
    @SerializedName("security")
    var security: String? = null
    @Expose
    @SerializedName("status")
    var status: String? = null
    @Expose
    @SerializedName("time")
    var time: String? = null
    @Expose
    @SerializedName("to_meet")
    var toMeet: String? = null
    @Expose
    @SerializedName("visitor_category")
    var visitorCategory: String? = null
    @Expose
    @SerializedName("visitor_img")
    var visitorImg: String? = null
    @Expose
    @SerializedName("mob")
    var mob: String? = null
    @Expose
    @SerializedName("dept")
    var dept: String? = null
    @Expose
    @SerializedName("designation")
    var designation: String? = null

    @Expose
    @SerializedName("accomplice_list")
    var associatesList: ArrayList<AssociatesItem>? = ArrayList()
}