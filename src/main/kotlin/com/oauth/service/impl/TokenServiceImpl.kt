package com.oauth.service.impl

import com.oauth.config.AppPropertiesConfig
import com.oauth.model.AuthJwtToken
import com.oauth.service.KeyService
import com.oauth.service.TokenService
import com.oauth.util.DateUtils
import com.oauth.util.JwtTokenUtil
import io.jsonwebtoken.Claims
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TokenServiceImpl(
    private val keyService: KeyService,
    private val appPropertiesConfig: AppPropertiesConfig
) : TokenService {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        const val TOKEN_TYPE = "Bearer"
        const val CLAIM_SCOPE = "scope"
        const val CLAIM_EXPIRE_IN = "expire_in"
        const val CLAIM_EMAIL = "email"
        const val CLAIM_USER_ID = "user_id"
    }

    override fun generateAuthToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): AuthJwtToken {
        logger.info("Start generate auth token for userId: $userId, tokenId: $tokenId")
        val accessToken = generateAccessToken(tokenId, email, userId, clientId, scopes)
        val refreshToken = generateRefreshToken(tokenId, email, userId, clientId, scopes)
        return AuthJwtToken(accessToken, refreshToken)
    }

    override fun generateAccessToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): String {
        logger.info("Start generate access token")
        val keypair = keyService.getAccessTokenKeypair()
        val tokenExpireTime = DateUtils.getNextMinute(appPropertiesConfig.expireAccessToken.toInt())
        val claims: MutableMap<String, Any> = HashMap()
        claims[CLAIM_SCOPE] = scopes
        claims[CLAIM_EMAIL] = email
        claims[CLAIM_USER_ID] = userId
        claims[CLAIM_EXPIRE_IN] = appPropertiesConfig.expireAccessToken.toInt() * 60 * 1000 //in second
        val token = JwtTokenUtil().encodeJwt(
            id = tokenId,
            claims = claims,
            isUser = appPropertiesConfig.iss,
            expireTime = tokenExpireTime,
            subject = userId,
            privateKey = keypair.private,
            audience = clientId
        )
        logger.info("Success generate access token")
        return token
    }

    override fun generateRefreshToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): String {
        logger.info("Start generate refresh token")
        val keypair = keyService.getRefreshTokenKeypair()
        val tokenExpireTime = DateUtils.getNextMinute(appPropertiesConfig.expireAccessToken.toInt())
        val claims: MutableMap<String, Any> = HashMap()
        claims[CLAIM_SCOPE] = scopes
        claims[CLAIM_EMAIL] = email
        claims[CLAIM_USER_ID] = userId
        claims[CLAIM_EXPIRE_IN] = appPropertiesConfig.expireRefreshToken.toInt() * 60 * 1000 //in second
        val token = JwtTokenUtil().encodeJwt(
            id = tokenId,
            claims = claims,
            isUser = appPropertiesConfig.iss,
            expireTime = tokenExpireTime,
            subject = userId,
            privateKey = keypair.private,
            audience = clientId
        )
        logger.info("Success generate refresh token")
        return token
    }

    override fun decodeAccessToken(token: String): Claims {
        val keypair = keyService.getAccessTokenKeypair()
        return JwtTokenUtil().decodeJwt(token, keypair.public)
    }


}