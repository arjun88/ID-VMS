package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorInfoApiResponse(

	@Expose
@field:SerializedName("visitorInfo")
	val visitorInfo: VisitorInfo? = null,

	@Expose
@field:SerializedName("message")
	val message: String? = null,

	@Expose
@field:SerializedName("status")
	val status: Boolean? = null
)

data class VisitorInfo(

	@Expose
@field:SerializedName("visitorName")
	val visitorName: String? = null,

	@Expose
@field:SerializedName("visitorCompany")
	val visitorCompany: String? = null,

	@Expose
@field:SerializedName("proofDetails")
	val proofDetails: String? = null,

	@Expose
@field:SerializedName("iDProofCode")
	val iDProofCode: String? = null,

	@Expose
@field:SerializedName("imageData")
	val imageData: String? = null,

	@Expose
@field:SerializedName("visitorMobile")
	val visitorMobile: String? = null,

	@Expose
@field:SerializedName("visitorEmail")
	val visitorEmail: String? = null,

	@Expose
@field:SerializedName("visitorID")
	val visitorID: Int? = null
)
