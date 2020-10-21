package com.idbsoftek.vms.setup.log_list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorLogListApiResponse(

	@Expose
	@field:SerializedName("visitorList")
	val visitorList: List<VisitorListItem>? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)

data class VisitorListItem(

	@Expose
	@field:SerializedName("associateDetails")
	val associateDetails: List<AscRecord>? = null,

	@Expose
	@field:SerializedName("visitorCompany")
	val visitorCompany: String? = null,

	@Expose
	@field:SerializedName("imageData")
	val imageData: String? = null,

	@Expose
	@field:SerializedName("imageName")
	val imageName: String? = null,

	@Expose
	@field:SerializedName("purposeCode")
	val purposeCode: String? = null,

	@Expose
	@field:SerializedName("categoryCode")
	val categoryCode: String? = null,

	@Expose
	@field:SerializedName("employeeFullName")
	val employeeFullName: String? = null,

	@Expose
	@field:SerializedName("departmentCode")
	val departmentCode: String? = null,

	@Expose
	@field:SerializedName("toDate")
	val toDate: String? = null,

	@Expose
	@field:SerializedName("visitorMobile")
	val visitorMobile: String? = null,

	@Expose
	@field:SerializedName("purposeName")
	val purposeName: String? = null,

	@Expose
	@field:SerializedName("visitorEmail")
	val visitorEmail: String? = null,

	@Expose
	@field:SerializedName("bodyTemp")
	val bodyTemp: String? = null,

	@Expose
	@field:SerializedName("vehicleNumber")
	val vehicleNumber: String? = null,

	@Expose
	@field:SerializedName("iDProofCode")
	val iDProofCode: String? = null,

	@Expose
	@field:SerializedName("proofDetails")
	val proofDetails: String? = null,


	@Expose
	@field:SerializedName("categoryName")
	val categoryName: String? = null,

	@Expose
	@field:SerializedName("fromDate")
	val fromDate: String? = null,

	@Expose
	@field:SerializedName("visitorName")
	val visitorName: String? = null,

	@Expose
	@field:SerializedName("requestID")
	val requestID: Int? = null,

	@Expose
	@field:SerializedName("rejectComment")
	val rejectComment: String? = null,

	@Expose
	@field:SerializedName("designationCode")
	val designationCode: String? = null,

	@Expose
	@field:SerializedName("visitorStatus")
	val visitorStatus: Int? = null,

	@Expose
	@field:SerializedName("visitorID")
	val visitorID: Int? = null,

	@Expose
	@field:SerializedName("isSelfApproved")
	val isSelfApproved: Boolean? = false,

	@Expose
	@field:SerializedName("movementStatus")
	val movementStatus: Int? = 0,

	@Expose
	@field:SerializedName("isOverStayed")
	val isOverStayed: Boolean? = false,

	@Expose
	@field:SerializedName("status")
	val status: Int? = null
)
