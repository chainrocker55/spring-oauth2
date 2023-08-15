package com.oauth.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthResponse(
    @JsonProperty("access_token")
    var accessToken: String? = null,
    @JsonProperty("refresh_token")
    var refreshToken: String? = null,
    @JsonProperty("token_type")
    var tokenType: String? = null,
    @JsonProperty("expires_in")
    var expiresIn: Int? = null,
    @JsonProperty("scope")
    var scope: String? = null
)
