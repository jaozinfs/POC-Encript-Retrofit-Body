package com.example.encryptedrest.api

import android.content.Context
import com.example.encryptedrest.utils.RequestBodyEncripted
import com.example.encryptedrest.utils.toRequestBodyEncripted
import okhttp3.Interceptor
import okhttp3.Response


class EncrptionInterceptor(private val applicationContext: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.body()
            .toRequestBodyEncripted()
            .setMediaType(RequestBodyEncripted.MediaTypes.AplicationJSON)
            .setPublicKey(...)
            .encriptData()
            .setEndpointArgs(Pair("/oauth/token", RequestBodyEncripted.RequestTypes.POST))
            .generateRequestBody()
            .build(request)

        return chain.proceed(newRequest)
    }

}