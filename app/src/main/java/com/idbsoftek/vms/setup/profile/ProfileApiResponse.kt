package com.idbsoftek.vms.setup.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileApiResponse(

	@field:SerializedName("message")
	@Expose
	val message: String? = "",


	@field:SerializedName("status")
	@Expose
	val status: Boolean? = false,

	@field:SerializedName("employeeList")
	@Expose
	val profileData: ProfileData? = null,
)