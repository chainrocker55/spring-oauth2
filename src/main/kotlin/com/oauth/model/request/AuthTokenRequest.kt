package com.oauth.model.request

import jakarta.validation.constraints.NotEmpty

data class AuthTokenRequest(
    @field:NotEmpty(message = "parameter may not be null or empty")
    val clientId: String,
    @field:NotEmpty(message = "parameter may not be null or empty")
    val userId: String,
    @field:NotEmpty(message = "parameter may not be null or empty")
    val email: String,
    @field:NotEmpty(message = "parameter may not be null")
    val scope: String,
)