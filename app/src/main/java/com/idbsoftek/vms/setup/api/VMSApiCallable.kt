package com.idbsoftek.vms.setup.api

import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.setup.form.GateListingApiResponse
import com.idbsoftek.vms.setup.form.SearchVisitorApiResponse
import com.idbsoftek.vms.setup.form.VisitorInfoApiResponse
import com.idbsoftek.vms.setup.form.VmsInitApiResponse
import com.idbsoftek.vms.setup.log_list.VisitorLogDetailsApiResponse
import com.idbsoftek.vms.setup.log_list.VisitorLogListApiResponse
import com.idbsoftek.vms.setup.self_checkin.RefNumDetailsApiResponse
import com.idbsoftek.vms.setup.visitor_stats.AdminVisitorStatsApiResponse
import com.idbsoftek.vms.setup.visitor_stats.DbVisitorStatsApiResponse
import com.idbsoftek.vms.setup.visitor_stats.StatsOfVisitorApiResponse
import com.idbsoftek.vms.setup.visitor_stats.VisitorStatsApiResponse
import io.reactivex.Observable

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
    fun getVisitorLogListApi(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Observable<VisitorLogApiResponse>

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

    // ID - Proof Lists
    @POST
    @FormUrlEncoded
    fun getIdProofs(
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
    fun getRefNumListSearch(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<VisRefNumListApiResponse>

    @POST
    @FormUrlEncoded
    fun fetchDetails(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("ref_num") refNum: String?,
        @Field("date") date: String?
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

    @GET
    fun loadVisitorsInDeptAnalytics(
        @Url url: String?,
        @Header("Authorization") userName: String?,
        @Query("FromDate") fromDate: String?,
        @Query("ToDate") toDate: String?,
        @Query("DepartmentCode") deptCode: String?
    ):
            Call<StatsOfVisitorApiResponse>

    @POST
    fun submitFormApi(
        @Url url: String?,
        @Body formData: RequestBody?,
        @Header("Authorization") token: String?
    ):
            Call<VisitorActionApiResponse>
    @POST
    fun selfApprovalApi(
        @Url url: String?,
        @Body formData: RequestBody?,
        @Header("Authorization") token: String?
    ):
            Call<VisitorActionApiResponse>

    //Dept Analytics Based On Date Range (Filter)

    @GET
    fun loadVDeptAnalyticsFilter(
        @Url url: String?,
        @Header("Authorization") token: String?,
        @Query("FromDate") fromDate: String?,
        @Query("ToDate") toDate: String?
    ):
            Call<DepartmentApiResponse>

    @POST
    @FormUrlEncoded
    fun loadVisitorStats(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<VisitorStatsApiResponse>

    @GET
    fun loadVisitorStatsAdmin(
        @Url url: String?,
        @Header("Authorization") token: String?,
        @Query("FromDate") fromDate: String?,
        @Query("ToDate") toDate: String?
    ):
            Call<DbVisitorStatsApiResponse>

    // SELF CHECK IN

    @POST
    @FormUrlEncoded
    fun getVisNumDetails(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("ref_num") refNum: String?
    ):
            Call<RefNumDetailsApiResponse>

    //

    @POST
    @FormUrlEncoded
    fun doSelfCheckIn(
        @Url url: String?,
        @Field("empID") userName: String?,
        @Field("session_id") sessionID: String?,
        @Field("vis_ref_num") refNum: String?,
        @Field("ve_id") veID: String?,
        @Field("gate") gate: String?,
        @Field("action") action: String?
    ):
            Call<CommonApiResponse>

    // UPDATED 2.0

    @GET
    fun getSettingsApi(
        @Url url: String?,
        @Header("Authorization") token: String?
    ):
            Call<VmsInitApiResponse>

    @GET
    fun getToMeetEmpApi(
        @Url url: String?,
        @Header("Authorization") token: String?
    ):
            Call<ToMeetApiResponse>

    //Search Visitor
    @GET
    fun searchVisitor(
        @Url url: String?,
        @Query("name") name: String?
    ): Call<SearchVisitorApiResponse>

    //Log List
    @GET
    fun getVisitorLogApi(
        @Url url: String?,
        @Header("Authorization") token: String?
    ): Observable<VisitorLogListApiResponse>

    // Auto-fill Visitor Form
    @GET
    fun getVisitorInfoApi(
        @Url url: String?,
        @Query("visitorId") visitorId: Int?
    ): Call<VisitorInfoApiResponse>

    // V 2.0

    @GET
    fun fetchLogDetailsApi(
        @Url url: String?,
        @Query("RequestId") reqID: String?,
        @Header("Authorization") token: String?
    ):
            Call<VisitorLogDetailsApiResponse>

    @POST
    fun doVisitorActionApi(
        @Url url: String?,
        @Header("Authorization") token: String?,
        @Body reqBody: RequestBody?
    ):
            Call<VisitorActionApiResponse>

    @GET
    fun getGatesApi(
        @Url url: String?,
        @Header("Authorization") token: String?
    ):
            Call<GateListingApiResponse>
}