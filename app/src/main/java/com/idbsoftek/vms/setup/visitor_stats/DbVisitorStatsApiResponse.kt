package com.idbsoftek.vms.setup.visitor_stats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DbVisitorStatsApiResponse(

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("dashboardList")
	val dashboardList: List<DashboardListItem>? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DashboardListItem(

	@Expose
	@field:SerializedName("departmentName")
	val departmentName: String? = null,

	@Expose
	@field:SerializedName("deptCode")
	val deptCode: String? = null,

	@Expose
	@field:SerializedName("meetCompleted")
	val meetCompleted: Int? = null,

	@Expose
	@field:SerializedName("approved")
	val approved: Int? = null,

	@Expose
	@field:SerializedName("checkIn")
	val checkIn: Int? = null,

	@Expose
	@field:SerializedName("meetInProgress")
	val meetInProgress: Int? = null,

	@Expose
	@field:SerializedName("pending")
	val pending: Int? = null,

	@Expose
	@field:SerializedName("checkOut")
	val checkOut: Int? = null,

	@Expose
	@field:SerializedName("totalCount")
	val totalCount: Int? = null,

	@Expose
	@field:SerializedName("overStayed")
	val overStayed: Int? = null
)
