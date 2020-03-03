package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GateListingApiResponse(
	@Expose
	@field:SerializedName("gates_listing")
	val gatesListing: List<GatesListingItem?>? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)