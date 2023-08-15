package com.oauth.service

import com.oauth.model.AuthJwtToken
import io.jsonwebtoken.Claims


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
    fun decodeAccessToken(token: String): Claims
}