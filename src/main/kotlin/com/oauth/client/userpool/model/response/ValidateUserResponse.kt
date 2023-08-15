package com.oauth.client.userpool.model.response

data class ValidateUserResponse(
    val email: String,
    val userId: Int,
    val externalCustomerId: String,
)