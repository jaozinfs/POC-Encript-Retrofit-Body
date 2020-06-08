package com.example.encryptedrest.utils

import android.content.Context
import android.content.res.Resources
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object EncryptUtils {
    @Throws(Exception::class)
    fun encrypt(plainText: String, publicKey: PublicKey?): String {
        val encryptCipher = Cipher.getInstance("RSA")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val cipherText =
            encryptCipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))
        return Base64.getEncoder().encodeToString(cipherText)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        URISyntaxException::class,
        IOException::class
    )
    fun getRSAPublicKey(context: Context): RSAPublicKey {

        val tbc = context.assets.open("tbc.pem")
        var publicKeyContent =
            String(tbc.readAllBytes)
        publicKeyContent =
            publicKeyContent.replace("\\n".toRegex(), "").replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
        val kf = KeyFactory.getInstance("RSA")
        val keySpecX509 =
            X509EncodedKeySpec(
                Base64.getDecoder().decode(publicKeyContent)
            )
        return kf.generatePublic(keySpecX509) as RSAPublicKey
    }

    private val InputStream.readAllBytes: ByteArray get(){
        val bufLen = 4 * 0x400 // 4KB
        val buf = ByteArray(bufLen)
        var readLen: Int
        var exception: IOException? = null
        try {
            ByteArrayOutputStream().use { outputStream ->
                while (read(buf, 0, bufLen)
                        .also { readLen = it } != -1
                ) outputStream.write(buf, 0, readLen)
                return outputStream.toByteArray()
            }
        } catch (e: IOException) {
            exception = e
            throw e
        } finally {
            if (exception == null) close() else try {
                close()
            } catch (e: IOException) {
                exception.addSuppressed(e)
            }
        }
    }
}