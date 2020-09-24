package com.idbsoftek.vms.setup.log_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateLogStatusPost(

	@Expose
@field:
SerializedName("Status")
	var status: Int? = null,

	@Expose
@field:SerializedName("RequestID")
	var requestID: Int? = null,

	@Expose
@field:SerializedName("RejectComment")
	var rejectComment: String? = null,

	@Expose
@field:SerializedName("gateCode")
	var gateCode: String? = null
)
