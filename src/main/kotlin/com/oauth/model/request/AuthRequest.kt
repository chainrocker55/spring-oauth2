package com.oauth.model.request

import jakarta.validation.constraints.NotBlank

data class AuthRequest(
    @field:NotBlank(message = "parameter may not be null or empty")
    val username: String,
    @field:NotBlank(message = "parameter may not be null or empty")
    val password: String,
    @field:NotBlank(message = "parameter may not be null or empty")
    val clientId: String,
)