package com.idbsoftek.vms.setup.log_list

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AscRecord(
	@Expose
@field:SerializedName("asAssetNumber")
	val asAssetNumber: Int? = null,

	@Expose
@field:SerializedName("ascVisitorEmail")
	val ascVisitorEmail: String? = null,

	@Expose
@field:SerializedName("ascVisitorID")
	val ascVisitorID: Int? = null,

	@Expose
@field:SerializedName("ascVisitorName")
	val ascVisitorName: String? = null,

	@Expose
@field:SerializedName("movementStatus")
	val movementStatus: Int? = null,

	@Expose
@field:SerializedName("ascVisitorMobile")
	val ascVisitorMobile: String? = null,

	@Expose
@field:SerializedName("asciDProofCode")
	val asciDProofCode: String? = null,

	@Expose
@field:SerializedName("ascImageData")
	val ascImageData: String? = null,

	@Expose
@field:SerializedName("asAssetName")
	val asAssetName: String? = null,

	@Expose
@field:SerializedName("asVehicleNumber")
	val asVehicleNumber: String? = null,

	@Expose
@field:SerializedName("ascBodyTemp")
	val ascBodyTemp: String? = null,

	@Expose
@field:SerializedName("ascProofDetails")
	val ascProofDetails: String? = null,

	@Expose
@field:SerializedName("visitorStatus")
	val visitorStatus: Int? = null,

	@Expose
@field:SerializedName("meetCompleteTm")
	val meetCompleteTm: String? = null
) : Parcelable
