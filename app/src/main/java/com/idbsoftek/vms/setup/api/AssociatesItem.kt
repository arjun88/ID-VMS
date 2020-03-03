package com.idbsoftek.vms.setup.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AssociatesItem(
    @Expose
    @field:SerializedName("mob")
    var mob: String? = null,

    @Expose
    @field:SerializedName("assets")
    var assets: String? = null,

    @Expose
    @field:SerializedName("visitor_id_num")
    var visitorIdNum: String? = null,

    @Expose
    @field:SerializedName("id_proof")
    var idProof: String? = null,

    @Expose
    @field:SerializedName("name")
    var name: String? = null,

    @Expose
    @field:SerializedName("id_num")
    var idNum: String? = null,

    @Expose
    @field:SerializedName("associate_photo")
    var associatePhoto: String? = null,

    @Expose
    @field:SerializedName("assets_num")
    var assetsNum: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0!!.writeString(mob)
        p0.writeString(assets)
        p0.writeString(visitorIdNum)
        p0.writeString(idProof)
        p0.writeString(name)
        p0.writeString(idNum)
        p0.writeString(assetsNum)
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<AssociatesItem> {
        override fun createFromParcel(parcel: Parcel): AssociatesItem {
            return AssociatesItem(parcel)
        }

        override fun newArray(size: Int): Array<AssociatesItem?> {
            return arrayOfNulls(size)
        }

    }
}



