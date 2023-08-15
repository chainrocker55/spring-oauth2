package com.oauth.model.request

import jakarta.validation.constraints.NotEmpty

data class AuthCodeRequest(
    @field:NotEmpty(message = "parameter may not be null or empty")
    val clientId: String,
    @field:NotEmpty(message = "parameter may not be null or empty")
    val userId: String,
    @field:NotEmpty(message = "parameter may not be null or empty")
    val email: String,
    @field:NotEmpty(message = "parameter may not be null or empty")
    val state: String,
    @field:NotEmpty(message = "parameter may not be null")
    val redirectUrl: String,
    @field:NotEmpty(message = "parameter may not be null")
    val scope: String,
)