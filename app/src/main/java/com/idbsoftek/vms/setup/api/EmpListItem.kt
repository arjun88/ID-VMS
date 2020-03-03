package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmpListItem(

	@Expose
	@field:SerializedName("code")
	var code: String? = null,

	@Expose
	@field:SerializedName("name")
	var name: String? = null,

	var selected: Boolean? = null,

	val emplCodeName: String? = "$code - $name"
)