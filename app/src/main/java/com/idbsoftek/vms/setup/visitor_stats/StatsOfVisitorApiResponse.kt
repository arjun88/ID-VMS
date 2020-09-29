package com.idbsoftek.vms.setup.visitor_stats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.log_list.VisitorListItem

data class StatsOfVisitorApiResponse(

	@Expose
@field:SerializedName("visitorListCount")
	val visitorListCount: List<VisitorListItem>? = null,

	@Expose
@field:SerializedName("message")
	val message: String? = null,

	@Expose
@field:SerializedName("status")
	val status: Boolean? = null
)

data class VisitorListCountItem(

	@Expose
@field:SerializedName("departmentName")
	val departmentName: String? = null,

	@Expose
@field:SerializedName("designationName")
	val designationName: String? = null,

	@Expose
@field:SerializedName("visitorCompany")
	val visitorCompany: String? = null,

	@Expose
@field:SerializedName("imageData")
	val imageData: String? = null,

	@Expose
@field:SerializedName("employeeFullName")
	val employeeFullName: String? = null,

	@Expose
@field:SerializedName("movementStatus")
	val movementStatus: Int? = null,

	@Expose
@field:SerializedName("associateCount")
	val associateCount: Int? = null,

	@Expose
@field:SerializedName("toDate")
	val toDate: String? = null,

	@Expose
@field:SerializedName("visitorMobile")
	val visitorMobile: String? = null,

	@Expose
@field:SerializedName("purposeName")
	val purposeName: String? = null,

	@Expose
@field:SerializedName("visitorEmail")
	val visitorEmail: String? = null,

	@Expose
@field:SerializedName("categoryName")
	val categoryName: String? = null,

	@Expose
@field:SerializedName("fromDate")
	val fromDate: String? = null,

	@Expose
@field:SerializedName("visitorName")
	val visitorName: String? = null,

	@Expose
@field:SerializedName("requestID")
	val requestID: Int? = null,

	@Expose
@field:SerializedName("visitorID")
	val visitorID: Int? = null,

	@Expose
@field:SerializedName("status")
	val status: Int? = null
)
