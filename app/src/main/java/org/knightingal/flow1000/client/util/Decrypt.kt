package org.knightingal.flow1000.client.util

import org.knightingal.flow1000.client.BuildConfig
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Decrypt {
    fun decrypt(encrypted: ByteArray?): ByteArray {
        val iv = "2017041621251234".toByteArray()
        val key = BuildConfig.PASSWORD.toByteArray()
        try {
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            val secretKey = SecretKeySpec(key, "AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            return cipher.doFinal(encrypted)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            throw e
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
            throw e
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            throw e
        } catch (e: BadPaddingException) {
            e.printStackTrace()
            throw e
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
            throw e
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
            throw e
        }
//        return null
    }
}