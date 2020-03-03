
package com.idbsoftek.vms.setup.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VisitorPurposeApiResponse {

    @Expose
    @SerializedName("message")
    private String mMessage;
    @Expose
    @SerializedName("status")
    private Boolean mStatus;
    @Expose
    @SerializedName("visitor_purpose_list")
    private ArrayList<VisitorPurposeList> mVisitorPurposeList;

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

    public ArrayList<VisitorPurposeList> getVisitorPurposeList() {
        return mVisitorPurposeList;
    }

    public void setVisitorPurposeList(ArrayList<VisitorPurposeList> visitorPurposeList) {
        mVisitorPurposeList = visitorPurposeList;
    }

}
