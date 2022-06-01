package com.plcoding.instagramui.saveplace.mainActivity

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypt {

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    fun generateKey(): String {


        val result = ByteArray(128 / 8)
        SecureRandom().nextBytes(result)

        //Log.d("aes", result.toHex())

        return result.toHex()
    }

    fun generateIv(): String {

        val result = ByteArray(128 / 8)
        SecureRandom().nextBytes(result)

        //Log.d("aes", result.toHex())


        return result.toHex()
    }


    fun encrypt(key: String, initVector: String, value: String): String? {
        try {
            //Log.d("aes", initVector.chunked(2).map{it.toInt(16).toByte()}.toByteArray().toHex())
            //Log.d("aes", key.chunked(2).map{it.toInt(16).toByte()}.toByteArray().toHex())

            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val iv = IvParameterSpec(initVector.chunked(2).map{it.toInt(16).toByte()}.toByteArray())
            val skeySpec = SecretKeySpec(key.chunked(2).map{it.toInt(16).toByte()}.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)

            val encrypted = cipher.doFinal(value.toByteArray())

            return String(Base64.getEncoder().encode(encrypted))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    fun decrypt(key: String, initVector: String, encrypted: String?): String? {
        try {
            //Log.d("aes", initVector.chunked(2).map{it.toInt(16).toByte()}.toByteArray().toHex())
            //Log.d("aes", key.chunked(2).map{it.toInt(16).toByte()}.toByteArray().toHex())

            val iv = IvParameterSpec(initVector.chunked(2).map{it.toInt(16).toByte()}.toByteArray())
            val skeySpec = SecretKeySpec(key.chunked(2).map{it.toInt(16).toByte()}.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

            val original = cipher.doFinal(Base64.getDecoder().decode(encrypted))

            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }



}