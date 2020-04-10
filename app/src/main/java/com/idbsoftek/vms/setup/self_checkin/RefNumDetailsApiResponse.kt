package com.idbsoftek.vms.setup.self_checkin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.idbsoftek.vms.setup.api.DeptList

data class RefNumDetailsApiResponse(

	@Expose
	@field:SerializedName("visitor_name")
	val name: String? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null,

	@Expose
	@field:SerializedName("to_meet")
	val toMeet: String? = null,

	@Expose
	@field:SerializedName("cur_status")
	val curStatus: String? = null,

	@Expose
	@field:SerializedName("ve_id")
	val veID: String? = null

)