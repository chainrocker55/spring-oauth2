package com.oauth.service

import com.oauth.client.keymanagement.response.AuthCryptoInfoFile
import java.security.KeyPair


interface KeyService {
    fun decryptCipherIfHasBracket(value: String, cryptoInfo: AuthCryptoInfoFile): String
    fun getRefreshTokenCryptoFile(): AuthCryptoInfoFile
    fun getAccessTokenCryptoFile(): AuthCryptoInfoFile
    fun getPassPhraseCryptoFile(): AuthCryptoInfoFile
    fun getAccessTokenKeypair(): KeyPair
    fun getRefreshTokenKeypair(): KeyPair
}