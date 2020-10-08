package com.idbsoftek.vms.setup.log_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MultiDayCheckInPost(

	@Expose
	@field:SerializedName("assetNumber")
	var assetNumber: Int? = 0,

	@Expose
	@field:SerializedName("requestID")
	var requestID: Int? = 0,

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
	@field:SerializedName("visitorID")
	var visitorID: Int? = 0,

	@Expose
	@field:SerializedName("status")
	var status: Int? = 0,

	@Expose
	@field:SerializedName("gateCode")
	var gateCode: String? = ""
)
