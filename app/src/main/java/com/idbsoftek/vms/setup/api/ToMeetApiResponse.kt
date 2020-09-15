package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.EmpListItem

data class ToMeetApiResponse(

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null,

	@Expose
	@field:SerializedName("employeeList")
	val empList: List<EmpListItem>? = null
)