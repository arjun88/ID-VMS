
package com.idbsoftek.vms.setup.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeptList implements Parcelable {

    @Expose
    @SerializedName("code")
    private String mCode;
    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("dept_code")
    private String dCode;

    @Expose
    @SerializedName("deptName")
    private String dName;

    public String getdCode() {
        return dCode;
    }

    public void setdCode(String dCode) {
        this.dCode = dCode;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public static Creator<DeptList> getCREATOR() {
        return CREATOR;
    }

    @Expose
    @SerializedName("numOfVisitors")
    private String numOfVisitors;

    public DeptList(){

    }

    private DeptList(Parcel in) {
        mCode = in.readString();
        mName = in.readString();
        numOfVisitors = in.readString();
    }

    public static final Creator<DeptList> CREATOR = new Creator<DeptList>() {
        @Override
        public DeptList createFromParcel(Parcel in) {
            return new DeptList(in);
        }

        @Override
        public DeptList[] newArray(int size) {
            return new DeptList[size];
        }
    };

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getNumOfVisitors() {
        return numOfVisitors;
    }

    public void setNumOfVisitors(String numOfVisitors) {
        this.numOfVisitors = numOfVisitors;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCode);
        parcel.writeString(mName);
        parcel.writeString(numOfVisitors);
    }
}
