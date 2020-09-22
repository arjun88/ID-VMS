package com.idbsoftek.vms.setup.login

import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.setup.profile.ProfileApiResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface LoginApiCallable {

    //Login
    @POST
    fun login(
        @Url url: String?,
        @Body loginPostData: RequestBody
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