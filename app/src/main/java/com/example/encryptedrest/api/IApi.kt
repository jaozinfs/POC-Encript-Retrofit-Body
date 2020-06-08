package com.example.encryptedrest.api

import com.example.encryptedrest.TesteBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IApi {

    @POST("/oauth/token")
    suspend fun teste(@Body testeBody: TesteBody): String

    @POST("/oauth/token2")
    suspend fun teste2(): String
}