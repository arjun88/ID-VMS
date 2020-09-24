package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GatesListingItem(

	@Expose
	@field:SerializedName("defaultGate")
	val isDefault: Boolean? = null,

	@Expose
	@field:SerializedName("gateCode")
	val code: String? = null,

	@Expose
	@field:SerializedName("gateName")
	val name: String? = null
)