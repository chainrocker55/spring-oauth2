package com.oauth.component

import com.oauth.client.keymanagement.response.AuthCryptoInfoFile
import com.oauth.client.keymanagement.response.KmsKeyPair
import jakarta.xml.bind.DatatypeConverter
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Component
class KeyMgmtComponent {

    fun decodeKeyPair(publicKey: String, privateKey: String): KeyPair {
        val decodedPrivate: ByteArray = Base64.getDecoder().decode(loadPemPrivateKeyString(privateKey))
        val decodedPublic: ByteArray = Base64.getDecoder().decode(loadPemPublicKeyString(publicKey))
        val keyFactory = KeyFactory.getInstance("RSA")
        return KeyPair(
            keyFactory.generatePublic(X509EncodedKeySpec(decodedPublic)),
            keyFactory.generatePrivate(PKCS8EncodedKeySpec(decodedPrivate))
        )
    }

    fun decodeKeyPair(keyPair: KmsKeyPair): KeyPair {
        val decodedPrivate: ByteArray = Base64.getDecoder().decode(keyPair.privateKey)
        val decodedPublic: ByteArray = Base64.getDecoder().decode(keyPair.publicKey)
        val keyFactory = KeyFactory.getInstance("RSA")
        return KeyPair(
            keyFactory.generatePublic(X509EncodedKeySpec(decodedPublic)),
            keyFactory.generatePrivate(PKCS8EncodedKeySpec(decodedPrivate))
        )
    }

    fun encryptCipher(srcText: String, cryptoInfo: AuthCryptoInfoFile): String {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec: KeySpec =
            PBEKeySpec(cryptoInfo.secret!!.toCharArray(), DatatypeConverter.parseHexBinary(cryptoInfo.salt), 1000, 256)
        val secret: SecretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(DatatypeConverter.parseHexBinary(cryptoInfo.iv)))
        val byteEncrypted = cipher.doFinal(srcText.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(byteEncrypted)
    }

    fun decryptCipher(cipherText: String, cryptoInfo: AuthCryptoInfoFile): String {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec: KeySpec =
            PBEKeySpec(cryptoInfo.secret!!.toCharArray(), DatatypeConverter.parseHexBinary(cryptoInfo.salt), 1000, 256)
        val secret: SecretKey = SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val cipherTextBinary = Base64.getDecoder().decode(cipherText)
        cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(DatatypeConverter.parseHexBinary(cryptoInfo.iv)))
        return String(cipher.doFinal(cipherTextBinary), StandardCharsets.UTF_8)
    }

    private fun loadPemPrivateKeyString(privateKey: String): String {
        return privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
    }

    private fun loadPemPublicKeyString(publicKey: String): String? {
        return publicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")
    }

}
