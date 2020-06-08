package com.example.encryptedrest.utils

import android.os.Build
import android.util.Log
import com.example.encryptedrest.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

fun OkHttpClient.Builder.getByApiVersion(): OkHttpClient.Builder {

    connectTimeout(60, TimeUnit.SECONDS)
    readTimeout(60, TimeUnit.SECONDS)
    writeTimeout(60, TimeUnit.SECONDS)

    if (Build.VERSION.SDK_INT in 16..21)
        followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .enableTlsOkhttpClient()
    return this
}

fun OkHttpClient.Builder.addLogInterceptor(): OkHttpClient.Builder {
    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        addNetworkInterceptor(logging)
    }
    return this
}

fun OkHttpClient.Builder.enableTlsOkhttpClient(): OkHttpClient.Builder {

    try {
        val sc = SSLContext.getInstance("TLSv1.2")
        sc.init(null, null, null)
        sslSocketFactory(Tls12SocketFactory(sc.socketFactory))

        val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .build()

        val specs = listOf(cs, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)
        connectionSpecs(specs)
    } catch (exc: Exception) {
        Log.d("OkHttpTLSCompat", "Error while setting TLS 1.2")
    }
    return this
}


internal const val apiDataFormat = "yyyy-MM-dd'T'HH:mm:ss"
val gsonDefault = GsonBuilder()
    .setDateFormat(apiDataFormat)
    .create()
val gsonBeautifulDefault = GsonBuilder()
    .setPrettyPrinting()
    .serializeNulls()
    .setDateFormat(apiDataFormat)
    .create()

fun Any.toBeautifulJson(): String {
    return gsonBeautifulDefault.toJson(this)

}