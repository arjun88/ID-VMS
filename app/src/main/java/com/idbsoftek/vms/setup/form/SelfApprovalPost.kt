package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SelfApprovalPost(

	@Expose
	@field:SerializedName("fromDate")
	var fromDate: String? = null,

	@Expose
	@field:SerializedName("visitorName")
	var visitorName: String? = null,

	@Expose
	@field:SerializedName("visitorCompany")
	var visitorCompany: String? = null,

	@Expose
	@field:SerializedName("toDate")
	var toDate: String? = null,

	@Expose
	@field:SerializedName("purposeCode")
	var purposeCode: String? = null,

	@Expose
	@field:SerializedName("origin")
	var origin: String? = null,

	@Expose
	@field:SerializedName("visitorMobile")
	var visitorMobile: String? = null,

	@Expose
	@field:SerializedName("employeeId")
	var employeeId: String? = null,

	@Expose
	@field:SerializedName("categoryCode")
	var categoryCode: String? = null,

	@Expose
	@field:SerializedName("visitorEmail")
	var visitorEmail: String? = null
)
