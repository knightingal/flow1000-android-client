package com.example.jianming.Utils

import com.example.jianming.myapplication.BuildConfig
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Decryptor {
    fun decrypt(encrypted: ByteArray?): ByteArray? {
        val iv = "2017041621251234".toByteArray()
        val key = BuildConfig.password.toByteArray()
        try {
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            val secretKey = SecretKeySpec(key, "AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            return cipher.doFinal(encrypted)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
        return null
    }
}