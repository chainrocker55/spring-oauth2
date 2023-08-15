package com.oauth.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthenticateDetail(
    val code: String,
    val clientName: String,
    val clientId: String,
    val clientSecret: String,
    val userId: String,
    val email: String,
    val redirectUri: String,
    val scope: String,
    val createdAt: Date? = Date()
): Serializable
