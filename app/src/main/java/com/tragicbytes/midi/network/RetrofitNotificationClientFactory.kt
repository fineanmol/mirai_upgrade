package com.tragicbytes.midi.network

import com.google.gson.GsonBuilder
import com.tragicbytes.midi.WooBoxApp
import com.tragicbytes.midi.utils.Constants
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitNotificationClientFactory {

    fun getRetroFitNotificationClient(): Retrofit {

        val builder = OkHttpClient().newBuilder().connectTimeout(3, TimeUnit.MINUTES).readTimeout(3, TimeUnit.MINUTES)

        builder.addInterceptor(NotificationResponseInterceptor()).build()

        val gson = GsonBuilder().setLenient().disableHtmlEscaping().create()

        return Retrofit.Builder().baseUrl(Constants.Config.NOTIFICATION_BASE_URL).addConverterFactory(
            GsonConverterFactory.create(gson)).build()
    }
}
class NotificationResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), response.body()!!.bytes())).build()
    }
}