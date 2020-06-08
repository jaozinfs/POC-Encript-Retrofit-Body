package com.example.encryptedrest.api

import android.app.Application
import com.example.encryptedrest.utils.addLogInterceptor
import com.example.encryptedrest.utils.getByApiVersion
import com.example.encryptedrest.utils.gsonDefault
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.coroutineContext

class RetrofitBuilderClient {
    companion object {

        fun <S> buildClient(
            url: String,
            api: Class<S>,
            application: Application
        ): S {

            val client = OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .getByApiVersion()
                .addLogInterceptor()

            client.addInterceptor(EncrptionInterceptor(application))

            return Retrofit.Builder()
                .baseUrl(url)
                .client(client.build())
                .addConverterFactory(
                    GsonConverterFactory.create(gsonDefault)
                )
                .build()
                .create(api)
        }

    }
}