package com.oauth.client.keymanagement.response

import com.fasterxml.jackson.annotation.JsonProperty

data class KmsKeyPair(
        @JsonProperty("privateKey")
        val privateKey: String,
        @JsonProperty("publicKey")
        val publicKey: String
)
