package com.idbsoftek.vms.setup.visitor_stats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatusListItem {
    @Expose
    @SerializedName("count")
    var count: String? = null

    @Expose
    @SerializedName("name")
    var name: String? = null
}