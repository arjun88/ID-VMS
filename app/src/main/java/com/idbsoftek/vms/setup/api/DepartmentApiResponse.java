
package com.idbsoftek.vms.setup.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DepartmentApiResponse {

    @Expose
    @SerializedName("dept_list")
    private ArrayList<DeptList> mDeptList;
    @Expose
    @SerializedName("message")
    private String mMessage;
    @Expose
    @SerializedName("status")
    private Boolean mStatus;

    @Expose
    @SerializedName("total_num_of_visitors")
    private String numOfVisitorsCount;

    @Expose
    @SerializedName("visitorInDepartments")
    private ArrayList<DeptList> deptFilterList;

    public ArrayList<DeptList> getDeptFilterList() {
        return deptFilterList;
    }

    public void setDeptFilterList(ArrayList<DeptList> deptFilterList) {
        this.deptFilterList = deptFilterList;
    }

    public ArrayList<DeptList> getmDeptList() {
        return mDeptList;
    }

    public void setmDeptList(ArrayList<DeptList> mDeptList) {
        this.mDeptList = mDeptList;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Boolean getmStatus() {
        return mStatus;
    }

    public void setmStatus(Boolean mStatus) {
        this.mStatus = mStatus;
    }

    public String getNumOfVisitorsCount() {
        return numOfVisitorsCount;
    }

    public void setNumOfVisitorsCount(String numOfVisitorsCount) {
        this.numOfVisitorsCount = numOfVisitorsCount;
    }

    public ArrayList<DeptList> getDeptList() {
        return mDeptList;
    }

    public void setDeptList(ArrayList<DeptList> deptList) {
        mDeptList = deptList;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

}
