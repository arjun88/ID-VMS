package com.idbsoftek.vms.setup.login

import android.content.Context
import android.util.Log
import com.idbsoftek.vms.api_retrofit.ApiClient
import com.idbsoftek.vms.api_retrofit.CommonApiResponse
import com.idbsoftek.vms.util.PrefUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenRefresh {
    private var tokenRefreshable: TokenRefreshable? = null
    fun getTokenRefreshInstance(tokenRefreshable: TokenRefreshable?): TokenRefresh {
        this.tokenRefreshable = tokenRefreshable
        Log.e("TOKEN Refresh ref IN:", "" + tokenRefreshable)
        //if (tokenRefreshInstance == null) {
        tokenRefreshInstance = TokenRefresh()
        //}
        return tokenRefreshInstance!!
    }

    fun setTokenRefInterface(tokenRefreshable: TokenRefreshable?){
        this.tokenRefreshable = tokenRefreshable
    }

    companion object {
        var tokenRefreshInstance: TokenRefresh? = null
    }

     fun doTokenRefresh(context: Context,  tokenRefreshSel: TokenRefreshable?) {
         //this.tokenRefreshable = tokenRefreshSel
        val prefUtil = PrefUtil(context)
        val apiCallable = ApiClient.getRetrofit()!!.create(
            LoginApiCallable::class.java
        )
        val url = "${PrefUtil.getBaseUrl()}Accounts/RefreshToken"
        apiCallable.refreshToken(
            url
           /* ,
            prefUtil.getApiToken()*/
        ).enqueue(object : Callback<CommonApiResponse> {
            override fun onResponse(
                call: Call<CommonApiResponse>,
                response: Response<CommonApiResponse>
            ) {
                when {
                    response.code() == 200 -> {
                            prefUtil.saveApiToken(response.body()!!.apiToken!!)
                        tokenRefreshSel!!.onTokenRefresh(200, response.body()!!.apiToken!!)
                    }
                    response.code() == 401 -> {
                        //if(tokenRefreshable != null)
                        tokenRefreshSel!!.onTokenRefresh(401, "")
                    }
                    response.code() == 500 -> {
                       // if(tokenRefreshable != null)
                        tokenRefreshSel!!.onTokenRefresh(500, "")
                    }
                    response.code() == 400 -> {
                       // if(tokenRefreshable != null)
                        tokenRefreshSel!!.onTokenRefresh(400, "")
                    }
                    else -> {
                       // if(tokenRefreshable != null)
                        tokenRefreshSel!!.onTokenRefresh(0, "")
                    }
                }
            }

            override fun onFailure(call: Call<CommonApiResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}