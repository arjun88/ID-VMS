
package com.idbsoftek.vms.setup.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisitorCategoryList {

    @Expose
    @SerializedName("code")
    private String mCode;
    @Expose
    @SerializedName("me_option")
    private Boolean mMeOption;
    @Expose
    @SerializedName("name")
    private String mName;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public Boolean getMeOption() {
        return mMeOption;
    }

    public void setMeOption(Boolean meOption) {
        mMeOption = meOption;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

}
