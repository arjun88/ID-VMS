package com.idbsoftek.vms.setup.analytics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AdminActionPost(

	@Expose
	@field:SerializedName("Status")
	var status: Int? = null,

	@Expose
	@field:SerializedName("requestID")
	var requestID: Int? = null,

	@Expose
	@field:SerializedName("StatusReason")
	var statusReason: String? = null,

	@Expose
	@field:SerializedName("StatusDtTm")
	var statusDtTm: String? = null,

	@Expose
	@field:SerializedName("gateCode")
	var gateCode: String? = null
)
