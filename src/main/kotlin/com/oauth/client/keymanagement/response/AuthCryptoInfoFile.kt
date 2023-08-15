package com.oauth.client.keymanagement.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthCryptoInfoFile(
    @JsonProperty("private_key")
    val privateKey: String,
    @JsonProperty("public_key")
    val publicKey: String,
    @JsonProperty("iv")
    val iv: String? = null,
    @JsonProperty("salt")
    val salt: String? = null,
    @JsonProperty("secret")
    val secret: String? = null,
)