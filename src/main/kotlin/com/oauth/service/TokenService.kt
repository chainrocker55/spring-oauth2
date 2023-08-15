package com.oauth.service

import com.oauth.model.AuthJwtToken


interface TokenService {
    fun generateAuthToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): AuthJwtToken

    fun generateAccessToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): String

    fun generateRefreshToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): String
}