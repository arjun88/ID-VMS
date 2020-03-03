package com.idbsoftek.vms.setup.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileApiResponse(

	@field:SerializedName("empid")
	@Expose
	val empid: String? = "",

	@field:SerializedName("mobilenumber")
	@Expose
	val mobilenumber: String? = "",

	@field:SerializedName("shift")
	@Expose
	val shift: String? = "",

	@field:SerializedName("message")
	@Expose
	val message: String? = "",

	@field:SerializedName("division")
	@Expose
	val division: String? = "",

	@field:SerializedName("imageurl")
	@Expose
	val imageurl: String? = "",

	@field:SerializedName("name")
	@Expose
	val name: String? = "",

	@field:SerializedName("location")
	@Expose
	val location: String? = "",

	@field:SerializedName("empdoj")
	@Expose
	val empdoj: String? = "",

	@field:SerializedName("designation")
	@Expose
	val designation: String? = "",

	@field:SerializedName("weekoff")
	@Expose
	val weekoff: String? = "",

	@field:SerializedName("department")
	@Expose
	val department: String? = "",

	@field:SerializedName("status")
	@Expose
	val status: Boolean? = false
)