package com.idbsoftek.vms.setup.form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VmsInitApiResponse(
	@Expose
	@field:SerializedName("vpSetting")
	val vpSetting: VpSetting? = null,

	@Expose
	@field:SerializedName("idproofList")
	val idproofList: List<IdproofListItem>? = null,

	@Expose
	@field:SerializedName("categoryList")
	val categoryList: List<CategoryListItem>? = null,

	@Expose
	@field:SerializedName("message")
	val message: String? = null,

	@Expose
	@field:SerializedName("purposeList")
	val purposeList: List<PurposeListItem>? = null,

	@Expose
	@field:SerializedName("status")
	val status: Boolean? = null
)

data class CategoryListItem(

	@Expose
	@field:SerializedName("categoryCode")
	val categoryCode: String? = null,

	@Expose
	@field:SerializedName("categoryName")
	val categoryName: String? = null
)

data class VpSetting(

	@Expose
	@field:SerializedName("isImageRequired")
	val isImageRequired: Boolean? = null,

	@Expose
	@field:SerializedName("printPassToAsc")
	val printPassToAsc: Boolean? = null,

	@Expose
	@field:SerializedName("canSaveAsDraft")
	val canSaveAsDraft: Boolean? = null,

	@Expose
	@field:SerializedName("isMeetCompleteRequired")
	val isMeetCompleteRequired: Boolean? = null,

	@Expose
	@field:SerializedName("vmcid")
	val vmcid: Int? = null,

	@Expose
	@field:SerializedName("printPass")
	val printPass: Boolean? = null,

	@Expose
	@field:SerializedName("associateInfoRequired")
	val associateInfoRequired: Boolean? = null,

	@Expose
	@field:SerializedName("isImageRequiredAsc")
	val isImageRequiredAsc: Boolean? = null
)

data class IdproofListItem(

	@Expose
	@field:SerializedName("iDProofName")
	val iDProofName: String? = null,

	@Expose
	@field:SerializedName("iDProofCode")
	val iDProofCode: String? = null
)

data class PurposeListItem(

	@Expose
	@field:SerializedName("purposeCode")
	val purposeCode: String? = null,

	@Expose
	@field:SerializedName("purposeName")
	val purposeName: String? = null
)
