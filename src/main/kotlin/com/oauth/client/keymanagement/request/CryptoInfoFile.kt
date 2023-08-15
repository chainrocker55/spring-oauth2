package com.oauth.client.keymanagement.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class CryptoInfoFile(
    val iv: String,
    val salt: String,
    val secret: String,
    @SerializedName("private_key")
    val privateKey: String,
    @SerializedName("public_key")
    val publicKey: String
) : java.io.Serializable
