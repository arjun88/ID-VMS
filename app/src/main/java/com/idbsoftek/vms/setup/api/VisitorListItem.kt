package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorListItem(

	@Expose
	@field:SerializedName("visitor_ref_num")
	var visitorRefNum: String? = null,

	@Expose
	@field:SerializedName("name")
	var name: String? = null
)