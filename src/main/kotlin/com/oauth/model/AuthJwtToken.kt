package com.oauth.model

data class AuthJwtToken(
    val accessToken: String,
    val refreshToken: String
)
