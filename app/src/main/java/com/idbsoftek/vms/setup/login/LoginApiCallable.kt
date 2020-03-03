package com.idbsoftek.vms.setup.login

import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.setup.profile.ProfileApiResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface LoginApiCallable {

    //Login
    @POST
    @FormUrlEncoded
    fun login(
        @Url url: String?,
        @Field("username") userName: String?,
        @Field("password") pwd: String?,
        @Field("fcm_key") fcmKey: String,
        @Field("imei") imei: String,
        @Field("app_version") appVer: String,
        @Field("os_version") osVer: String,
        @Field("phone_model") model: String
    ):
            Call<CommonApiResponse>

    //Company Code Enter
    @POST
    @FormUrlEncoded
    fun companyCodeValidation(
        @Url url: String?,
        @Field("clientid") companyCode: String?
    ):
            Call<CommonApiResponse>

    //PROFILE
    @POST
    @FormUrlEncoded
    fun getProfile(
        @Url url: String?,
        @Field("empID") username: String?,
        @Field("session_id") sessionID: String?
    ):
            Call<ProfileApiResponse>
}