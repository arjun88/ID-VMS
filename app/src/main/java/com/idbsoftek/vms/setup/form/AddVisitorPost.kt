package com.idbsoftek.vms.setup.form

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddVisitorPost(

	@Expose
	@field:SerializedName("visitorPassID")
	var visitorPassID: Int? = null,

	@Expose
	@field:SerializedName("visitorCompany")
	var visitorCompany: String? = null,

	@Expose
	@field:SerializedName("proofDetails")
	var proofDetails: String? = null,

	@Expose
	@field:SerializedName("comments")
	var comments: String? = null,

	@Expose
	@field:SerializedName("iDProofCode")
	var iDProofCode: String? = null,

	@Expose
	@field:SerializedName("imageData")
	var imageData: String? = null,

	@Expose
	@field:SerializedName("associateCount")
	var associateCount: Int? = null,

	@Expose
	@field:SerializedName("toDate")
	var toDate: String? = null,

	@Expose
	@field:SerializedName("visitorMobile")
	var visitorMobile: String? = null,

	@Expose
	@field:SerializedName("employeeId")
	var employeeId: String? = null,

	@Expose
	@field:SerializedName("bodyTemp")
	var bodyTemp: String? = null,

	@Expose
	@field:SerializedName("categoryCode")
	var categoryCode: String? = null,

	@Expose
	@field:SerializedName("visitorEmail")
	var visitorEmail: String? = null,

	@Expose
	@field:SerializedName("fromDate")
	var fromDate: String? = null,

	@Expose
	@field:SerializedName("AssociateDetails")
	var asc: List<AscItem>? = null,

	@Expose
	@field:SerializedName("visitorName")
	var visitorName: String? = null,

	@Expose
	@field:SerializedName("assetNumber")
	var assetNumber: Int? = null,

	@Expose
	@field:SerializedName("purposeCode")
	var purposeCode: String? = null,

	@Expose
	@field:SerializedName("assetName")
	var assetName: String? = null,

	@Expose
	@field:SerializedName("vehicleNumber")
	var vehicleNumber: String? = null,

	@Expose
	@field:SerializedName("visitorID")
	var visitorID: Int? = null,

	@Expose
	@field:SerializedName("oxygenSaturation")
	var oxygenSaturation: String? = null

	)

data class AscItem(

	@Expose
	@field:SerializedName("asAssetNumber")
	var asAssetNumber: Int? = null,

	@Expose
	@field:SerializedName("ascVisitorEmail")
	var ascVisitorEmail: String? = null,

	@Expose
	@field:SerializedName("ascImageData")
	var ascImageData: String? = null,

	@Expose
	@field:SerializedName("ascVisitorID")
	var ascVisitorID: Int? = null,

	@Expose
	@field:SerializedName("asAssetName")
	var asAssetName: String? = null,

	@Expose
	@field:SerializedName("asVehicleNumber")
	var asVehicleNumber: String? = null,

	@Expose
	@field:SerializedName("ascBodyTemp")
	var ascBodyTemp: String? = null,

	@Expose
	@field:SerializedName("ascOxygenSaturation")
	var ascOxygenSaturation: String? = null,

	@Expose
	@field:SerializedName("ascVisitorName")
	var ascVisitorName: String? = null,

	@Expose
	@field:SerializedName("ascProofDetails")
	var ascProofDetails: String? = null,

	@Expose
	@field:SerializedName("ascVisitorMobile")
	var ascVisitorMobile: String? = null,

	@Expose
	@field:SerializedName("asciDProofCode")
	var asciDProofCode: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<AscItem> {
        override fun createFromParcel(parcel: Parcel): AscItem {
            return AscItem(parcel)
        }

        override fun newArray(size: Int): Array<AscItem?> {
            return arrayOfNulls(size)
        }
    }
}
