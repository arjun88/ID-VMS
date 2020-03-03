package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.AssociatesItem
import com.idbsoftek.vms.setup.api.VisitDetail

class VMSDetailsApiResponse {
    @Expose
    @SerializedName("accomplice_list")
    var accompliceList: ArrayList<AssociatesItem>? = ArrayList()
    @Expose
    @SerializedName("message")
    var message: String? = null
    @Expose
    @SerializedName("status")
    var status: Boolean? = null
    @Expose
    @SerializedName("visit_details")
    var visitDetails: List<VisitDetail>? = null

}