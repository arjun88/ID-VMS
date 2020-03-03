package com.idbsoftek.vms.setup.api

import com.google.gson.annotations.SerializedName

data class Associate(

	@field:SerializedName("mob")
	val mob: String? = null,

	@field:SerializedName("id_number")
	val idNumber: String? = null,

	@field:SerializedName("assets")
	val assets: String? = null,

	@field:SerializedName("id_proof")
	val idProof: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("associate_photo")
	val associatePhoto: String? = null
)