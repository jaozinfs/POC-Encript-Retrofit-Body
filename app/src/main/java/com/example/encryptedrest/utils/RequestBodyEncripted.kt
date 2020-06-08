package com.example.encryptedrest.utils

import android.content.Context
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import java.security.PublicKey

/**
 * @author João Victor Oliveira
 *
 * Class de fabricação de request encriptada
 *
 */
typealias RequestType = String

class RequestBodyEncripted(private val data: RequestBody?) {
    object MediaTypes {
        val AplicationJSON = MediaType.parse("application/json; charset=utf-8")
    }

    object RequestTypes {
        val POST = "POST"
        const val GET = "GET"
        const val DELETE = "DELETE"
        const val UPDATE = "UPDATE"
    }

    /**
     * Header media type que será enviado no body encriptado
     */
    private var mediaType: MediaType? = null

    /**
     * valor do body encriptado
     */
    private var encriptedBody: String? = null

    /**
     * Chave publica para encryptar o valor do body
     */
    private var publicKey: PublicKey? = null

    /**
     * Body encriptado
     */
    private lateinit var encriptedRequestBody: RequestBody

    /**
     * lista de endpoints do filtro passado no build para retornar o novo request
     */
    private var listEndPoinsts: List<Pair<String, RequestType>>? = null

    fun setMediaType(mediaType: MediaType?): RequestBodyEncripted {
        this@RequestBodyEncripted.mediaType = mediaType
        return this
    }
//
//    /**
//     * Seta a chave publica RSA com chave estatica
//     *
//     */
//    fun setPublicKey(context: Context): RequestBodyEncripted {
//        publicKey = EncryptUtils.getRSAPublicKey(context)
//        return this
//    }

    /**
     * Seta a chave publica para passar no body
     *
     */
    fun setPublicKey(publicKey: PublicKey): RequestBodyEncripted {
        this@RequestBodyEncripted.publicKey = publicKey
        return this
    }

    /**
     * Encripta o body atual com a chave publica selecionada
     */
    fun encriptData(): RequestBodyEncripted {
        encriptedBody = EncryptUtils.encrypt(
            data?.string ?: throw RequestBodyEmpty("Request body nao setado"),
            publicKey ?: throw PublicKeyException("Nenhuma public key")
        )
        return this
    }

    /**
     * Genereta new Request body with new MEDIA TYPE and ENCRYPTED BODY DATA
     *
     */
    fun generateRequestBody(): RequestBodyEncripted {
        encriptedRequestBody = RequestBody.create(mediaType, encriptedBody)
        return this
    }

    /**
     *
     * NÃO ESQUEÇA DE CHAMAR A FUNÇÃO [generateRequestBody] ANTES DE CHAMAR ESTA !
     *
     *
     * caso tenha uma lista de filtros -> [listEndPoinsts], é verificado se a chamada bate com algum.
     * Caso não bate, é retornado o request original, caso bater é retornado request com body encriptado.
     * Se não houver lista de filtros, é encriptado de qualquer maneira.
     */
    fun build(request: Request): Request {

        listEndPoinsts.takeIf { it != null && it.isNotEmpty() }?.apply {
            filter {
                it.first == request.url().encodedPath() && it.second == request.method()
            }.takeIf { it.isNotEmpty() } ?: return request
        }

        return request.newBuilder().run {
            method(request.method(), encriptedRequestBody)
                .build()
        }
    }

    /**
     * @param endpoints -> É usado no filtro da requesição, se a chamada atual bater com algum dos valores
     * o body é encriptado, caso não bater, é retornada o body sem encript.
     * Se não passar nenhum filtro, irá encriptar de qualquer maneira.
     *
     */
    fun setEndpointArgs(vararg endpoints: Pair<String, RequestType>): RequestBodyEncripted {
        listEndPoinsts = listOf(*endpoints)
        return this
    }

    private val RequestBody.string: String
        get() {
            return try {
                Buffer().run {
                    writeTo(this)
                    readUtf8()
                }
            } catch (e: Exception) {
                throw java.lang.Exception("cannot write")
            }
        }
}


fun RequestBody?.toRequestBodyEncripted(): RequestBodyEncripted {
    return RequestBodyEncripted(this)
}

data class PublicKeyException(val error: String, val exception: Throwable? = null) :
    Exception(error, exception)

data class RequestBodyEmpty(val error: String, val exception: Throwable? = null) :
    Exception(error, exception)
