
package com.idbsoftek.vms.setup.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VisitorLogList {

    @Expose
    @SerializedName("assets")
    private String assets;
    @Expose
    @SerializedName("company")
    private String company;
    @Expose
    @SerializedName("date")
    private String date;
    @Expose
    @SerializedName("id_number")
    private String idNumber;
    @Expose
    @SerializedName("id_proof")
    private String idProof;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("purpose")
    private String purpose;
    @Expose
    @SerializedName("ref_num")
    private String refNum;
    @Expose
    @SerializedName("security")
    private String security;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("to_meet")
    private String toMeet;
    @Expose
    @SerializedName("visitor_category")
    private String visitorCategory;
    @Expose
    @SerializedName("visitor_img")
    private String visitorImg;

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToMeet() {
        return toMeet;
    }

    public void setToMeet(String toMeet) {
        this.toMeet = toMeet;
    }

    public String getVisitorCategory() {
        return visitorCategory;
    }

    public void setVisitorCategory(String visitorCategory) {
        this.visitorCategory = visitorCategory;
    }

    public String getVisitorImg() {
        return visitorImg;
    }

    public void setVisitorImg(String visitorImg) {
        this.visitorImg = visitorImg;
    }

}
