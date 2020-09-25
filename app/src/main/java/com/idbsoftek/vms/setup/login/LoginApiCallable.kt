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
    @GET
    fun getProfile(
        @Url url: String?,
        @Header("Authorization") token: String?
    ):
            Call<ProfileApiResponse>

    //Refresh Token


    @GET
//    @FormUrlEncoded
   /* @Headers(
        "Content-Type: Application/Json;charset=UTF-8",
        "Accept: Application/Json")*/
    fun refreshToken(
        @Url url: String?
       /* ,
       @Field("Authorization") token: String?*/
    ):
            Call<CommonApiResponse>

}