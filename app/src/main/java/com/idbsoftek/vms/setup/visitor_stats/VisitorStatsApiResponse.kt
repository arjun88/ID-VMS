package com.idbsoftek.vms.setup.visitor_stats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.DeptList

data class VisitorStatsApiResponse(

	@Expose
	@field:SerializedName("dashboard_data")
	val statsList: List<StatusListItem>? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)