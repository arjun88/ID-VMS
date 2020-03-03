package com.idbsoftek.vms.setup.api

import com.idbsoftek.vms.setup.form.GateListingApiResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface VMSApiCallable {

    @POST
    @FormUrlEncoded
    fun getVisitorLogList(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<VisitorLogApiResponse>

    @POST
    @FormUrlEncoded
    fun getGates(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<GateListingApiResponse>

    //APPLY FILTER MGR
    @POST
    @FormUrlEncoded
    fun aplyFilterMgr(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("dept_id") dept: String?,
        @Field("purpose_id") purpose: String?,
        @Field("visitor_category") visitorCategory: String?,
        @Field("from_date") fromDate: String?,
        @Field("to_date") toDate: String?
    ):
            Call<VisitorLogApiResponse>

    //APPLY FILTER SEC
    @POST
    @FormUrlEncoded
    fun aplyFilterSec(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("visitor_ref_num") refNum: String?,
        @Field("to_meet") toMeet: String?,
        @Field("visitor_category") visitorCategory: String?,
        @Field("from_date") fromDate: String?,
        @Field("to_date") toDate: String?
    ):
            Call<VisitorLogApiResponse>

    @POST
    @FormUrlEncoded
    fun getVisitorCategories(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<VisitorCategoryApiResponse>

    @POST
    @FormUrlEncoded
    fun getVisitorPurpose(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<VisitorPurposeApiResponse>

    @POST
    @FormUrlEncoded
    fun getDepartments(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<DepartmentApiResponse>

    //FOR SECURITY ************
    @POST
    @FormUrlEncoded
    fun getToMeetList(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<ToMeetApiResponse>

    @POST
    @FormUrlEncoded
    fun getRefNumList(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<RefNumListApiResponse>

    @POST
    @FormUrlEncoded
    fun fetchDetails(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("ref_num") refNum: String?
    ):
            Call<VMSDetailsApiResponse>


    @POST
    @FormUrlEncoded
    fun doVisitorAction(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("ref_num") refNum: String?,
        @Field("action") action: String?
    ):
            Call<VisitorActionApiResponse>

//    ANALYTICS

    @POST
    @FormUrlEncoded
    fun loadDashboardForAnalytics(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<AnalyticsDashboardApiResponse>

    @POST
    @FormUrlEncoded
    fun loadDeptAnalytics(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<DeptAnalyticsApiResponse>

    @POST
    @FormUrlEncoded
    fun loadVisitorsInDeptAnalytics(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("dept_code") deptCode: String?,
        @Field("from_date") fromDate: String?,
        @Field("to_date") toDate: String?
    ):
            Call<VisitorLogApiResponse>

    @POST
    fun submitFormApi(
        @Url url: String?,
        @Body formData: RequestBody?
    ):
            Call<VisitorActionApiResponse>

    //Dept Analytics Based On Date Range (Filter)

    @POST
    @FormUrlEncoded
    fun loadVDeptAnalyticsFilter(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("from_date") fromDate: String?,
        @Field("to_date") toDate: String?
    ):
            Call<DepartmentApiResponse>
}