package com.oauth.util

import java.security.SecureRandom
import java.util.*

object SecurityUtils {
    fun generateRandomString(): String {
        val randomGenerator = SecureRandom()
        val byte = ByteArray(16)
        randomGenerator.nextBytes(byte)
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val code = encoder.encodeToString(byte);
        return code
    }

}
