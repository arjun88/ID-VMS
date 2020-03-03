package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AnalyticsDashboardApiResponse(

	@Expose
	@field:SerializedName("numOfVisitorsToday")
	val numOfVisitorsToday: String? = null,

	@Expose
	@field:SerializedName("numOfDepartments")
	val numOfDepartments: String? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)