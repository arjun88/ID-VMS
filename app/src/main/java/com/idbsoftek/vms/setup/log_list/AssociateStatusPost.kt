package com.idbsoftek.vms.setup.log_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AssociateStatusPost(

	@Expose
	@field:
	SerializedName("Status")
	var status: Int? = null,

	@Expose
	@field:SerializedName("RequestID")
	var requestID: Int? = null,

	@Expose
	@field:SerializedName("visitorId")
	var visitorID: Int? = null,

	@Expose
	@field:SerializedName("gateCode")
	var gateCode: String? = null,

	@Expose
	@field:SerializedName("oxygenSaturation")
	var oxygenSaturation: String? = "",

	@Expose
	@field:SerializedName("assetName")
	var assetName: String? = "",

	@Expose
	@field:SerializedName("vehicleNumber")
	var vehicleNumber: String? = "",

	@Expose
	@field:SerializedName("bodyTemp")
	var bodyTemp: String? = "",

	@Expose
	@field:SerializedName("assetNumber")
	var assetNumber: Int? = 0
)
