package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.DeptList

data class DeptAnalyticsApiResponse(

	@Expose
	@field:SerializedName("visitorInDepartments")
	val visitorInDepartments: List<DeptList>? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)