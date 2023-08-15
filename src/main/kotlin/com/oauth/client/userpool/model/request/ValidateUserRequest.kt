package com.invx.oauth.client.admintool.model.request

data class ValidateUserRequest(
    val username: String,
    val password: String
)