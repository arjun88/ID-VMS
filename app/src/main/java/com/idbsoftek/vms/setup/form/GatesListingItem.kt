package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GatesListingItem(

	@Expose
	@field:SerializedName("isDefault")
	val isDefault: Boolean? = null,

	@Expose
	@field:SerializedName("code")
	val code: String? = null,

	@Expose
	@field:SerializedName("name")
	val name: String? = null
)