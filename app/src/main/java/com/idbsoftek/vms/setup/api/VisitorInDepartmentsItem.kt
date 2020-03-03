package com.idbsoftek.vms.setup.api

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitorInDepartmentsItem(

    @Expose
    @field:SerializedName("dept_code")
    val deptCode: String? = null,

    @Expose
    @field:SerializedName("deptName")
    val deptName: String? = null,

    @Expose
    @field:SerializedName("numOfVisitors")
    val numOfVisitors: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deptCode)
        parcel.writeString(deptName)
        parcel.writeString(numOfVisitors)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisitorInDepartmentsItem> {
        override fun createFromParcel(parcel: Parcel): VisitorInDepartmentsItem {
            return VisitorInDepartmentsItem(parcel)
        }

        override fun newArray(size: Int): Array<VisitorInDepartmentsItem?> {
            return arrayOfNulls(size)
        }
    }
}