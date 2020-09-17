package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchVisitorApiResponse(

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("searchResults")
	val searchResults: List<SearchResultsItem>? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)

data class SearchResultsItem(

	@Expose
	@field:SerializedName("visitorName")
	val visitorName: String? = null,

	@Expose
	@field:SerializedName("visitorCompany")
	val visitorCompany: String? = null,

	@Expose
	@field:SerializedName("visitorMobile")
	val visitorMobile: String? = null,

	@Expose
	@field:SerializedName("visitorEmail")
	val visitorEmail: String? = null,

	@Expose
	@field:SerializedName("visitorID")
	val visitorID: String? = null,

	var selected: Boolean? = false,
)
