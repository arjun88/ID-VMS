
package com.idbsoftek.vms.setup.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VisitorCategoryApiResponse {

    @Expose
    @SerializedName("message")
    private String mMessage;
    @Expose
    @SerializedName("status")
    private Boolean mStatus;
    @Expose
    @SerializedName("visitor_category_list")
    private ArrayList<VisitorCategoryList> mVisitorCategoryList;

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

    public ArrayList<VisitorCategoryList> getVisitorCategoryList() {
        return mVisitorCategoryList;
    }

    public void setVisitorCategoryList(ArrayList<VisitorCategoryList> visitorCategoryList) {
        mVisitorCategoryList = visitorCategoryList;
    }

}
