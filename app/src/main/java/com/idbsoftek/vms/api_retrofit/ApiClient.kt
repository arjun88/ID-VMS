package com.idbsoftek.vms.api_retrofit

import com.google.gson.GsonBuilder
import com.idbsoftek.vms.util.AppUtil
import com.idbsoftek.vms.util.PrefUtil

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null

        private val httpClient: OkHttpClient
            get() {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                return OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build()
            }

        fun getRetrofit(): Retrofit? {
            val gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .create()

            if (retrofit != null) {
                retrofit = null
            }

            var url = PrefUtil.getBaseUrl()
            if (url == null) {
                url = AppUtil.BASE_URL
            }

            val unSafeHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .client(unSafeHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

            return retrofit
        }
    }

}
