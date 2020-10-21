package com.idbsoftek.vms.setup.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileData(

	@Expose
@field:SerializedName("isInActive")
	val isInActive: Boolean? = null,

	@Expose
@field:SerializedName("employeeFullName")
	val employeeFullName: String? = null,

	@Expose
@field:SerializedName("imageName")
	val imageData: String? = null,

	@Expose
@field:SerializedName("departmentCode")
	val departmentCode: String? = null,

	@Expose
@field:SerializedName("designationMaster")
	val designationMaster: Any? = null,

	@Expose
@field:SerializedName("employeeFirstName")
	val employeeFirstName: String? = null,

	@Expose
@field:SerializedName("employeeGender")
	val employeeGender: String? = null,

	@Expose
@field:SerializedName("visitRequest")
	val visitRequest: Any? = null,

	@Expose
@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@Expose
@field:SerializedName("divisionMaster")
	val divisionMaster: Any? = null,

	@Expose
@field:SerializedName("isApprover")
	val isApprover: Boolean? = null,

	@Expose
@field:SerializedName("departmentMaster")
	val departmentMaster: Any? = null,

	@Expose
@field:SerializedName("companyMaster")
	val companyMaster: Any? = null,

	@Expose
@field:SerializedName("companyCode")
	val companyCode: String? = null,

	@Expose
@field:SerializedName("locationMaster")
	val locationMaster: Any? = null,

	@Expose
@field:SerializedName("approverId")
	val approverId: String? = null,

	@Expose
@field:SerializedName("employeeEmail")
	val employeeEmail: String? = null,

	@Expose
@field:SerializedName("employeeId")
	val employeeId: String? = null,

	@Expose
@field:SerializedName("approverLevel2")
	val approverLevel2: Any? = null,

	@Expose
@field:SerializedName("divisionCode")
	val divisionCode: String? = null,

	@Expose
@field:SerializedName("approverLevel1")
	val approverLevel1: Any? = null,

	@Expose
@field:SerializedName("employeeTitle")
	val employeeTitle: String? = null,

	@Expose
@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@Expose
@field:SerializedName("employeeLastName")
	val employeeLastName: String? = null,

	@Expose
@field:SerializedName("employeeMobile")
	val employeeMobile: String? = null,

	@Expose
@field:SerializedName("designationCode")
	val designationCode: String? = null,

	@Expose
@field:SerializedName("locationCode")
	val locationCode: String? = null
)
