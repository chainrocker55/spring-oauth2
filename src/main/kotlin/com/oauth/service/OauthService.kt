package com.oauth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.invx.oauth.client.admintool.config.UserPoolConfig
import com.invx.oauth.client.admintool.model.request.ValidateUserRequest
import com.oauth.client.userpool.const.UserPoolCode
import com.oauth.client.userpool.model.response.UserPoolBaseResponse
import com.oauth.client.userpool.model.response.ValidateUserResponse
import com.oauth.client.userpool.rest.UserPoolClient
import com.oauth.config.AppPropertiesConfig
import com.oauth.service.impl.TokenServiceImpl
import com.oauth.constant.RedisConstants
import com.oauth.data.entity.OauthClientEntity
import com.oauth.data.repository.OauthClientRepository
import com.oauth.exception.InternalServerException
import com.oauth.exception.InvalidRequestException
import com.oauth.exception.UnAuthenticatedException
import com.oauth.model.AuthenticateDetail
import com.oauth.model.request.AuthRequest
import com.oauth.model.response.AuthResponse
import com.oauth.util.RedisUtil
import org.apache.http.client.utils.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import java.util.*


@Service
class OauthService(
    private val oauthClientRepository: OauthClientRepository,
    private val appPropertiesConfig: AppPropertiesConfig,
    private val adminToolClient: UserPoolClient,
    private val adminToolConfig: UserPoolConfig,
    private val redisUtil: RedisUtil,
    private val tokenService: TokenService,
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        private val objectMapper: ObjectMapper = ObjectMapper()
    }

    fun authorizeCodeFlow(
        clientId: String,
        redirectUri: String,
        reqScope: List<String>,
        state: String,
        queryString: String,
    ): String {
        val client = getClientIsEnable(clientId)
        val uri = parseUrl(redirectUri)
        val clientUrl = client.redirectUrl.split(",")
        if (clientUrl.contains(uri.host)) {
            logger.error("Client redirect uri is invalid client_name: ${client.name}")
            throw UnAuthenticatedException("Client redirect uri is invalid")
        }

        val clientScope = client.clientScope.map { it.scopes.name }.toSet()
        if (!isInScope(scopes = reqScope, scopeSet = clientScope)) {
            logger.error("Client scope is invalid client_name: ${client.name}")
            throw UnAuthenticatedException("Client scope is invalid")
        }

        return "${appPropertiesConfig.loginUrl}?$queryString"

    }

    fun checkClient(clientId: String): Boolean {
        getClientIsEnable(clientId)
        return true
    }

    fun getClientIsEnable(clientId: String): OauthClientEntity {
        val clientOp = oauthClientRepository.findById(clientId)
        if (clientOp.isEmpty) {
            logger.error("Not found this client_id: $clientId")
            throw UnAuthenticatedException("Not found this client")
        }
        val client = clientOp.get()
        if (!client.isEnabled) {
            logger.error("Client is disabled client_id: $clientId client_name: ${client.name}")
            throw UnAuthenticatedException("Client ${client.name} is disabled")
        }
        return client
    }

    fun validateUser(request: AuthRequest): UserPoolBaseResponse<ValidateUserResponse> {
        if (adminToolConfig.mock == true) {
            return getValidateUserMockMode(request.username, request.password)
        }
        try {
            val response = adminToolClient.validateUser(ValidateUserRequest(request.username, request.password))
            logger.info(
                "Validate user from ei response status code: {} and message: {} ",
                response.code,
                response.message
            )
            return response
        } catch (ex: Exception) {
            logger.error("Call admin-tool service error", ex)
            throw InternalServerException(message = "Call admin-tool service error")
        }
    }

    fun exchangeCode(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String
    ): AuthResponse {
        val authenticateDetailOp = getAuthenticateDetail(code)
        if (authenticateDetailOp.isEmpty) {
            logger.error("Not found this code for client_id: $clientId")
            throw UnAuthenticatedException("Not found this code for client_id: $clientId")
        }
        val authenticateDetail = authenticateDetailOp.get()
        if (authenticateDetail.clientId != clientId && authenticateDetail.clientSecret != clientSecret) {
            logger.error("Client is not match, client_id: $clientId")
            throw UnAuthenticatedException("Client secret is invalid")
        }
        if (redirectUri != authenticateDetail.redirectUri) {
            logger.error("Client redirect uri is invalid client_id: $clientId")
            throw UnAuthenticatedException("Client redirect uri is invalid client_id: $clientId")
        }
        val id = UUID.randomUUID().toString()

        val authJwt =
            tokenService.generateAuthToken(
                tokenId = id,
                email = authenticateDetail.email,
                userId = authenticateDetail.userId,
                clientId = clientId,
                scopes = authenticateDetail.scope
            )
        val tokenExpireTime = appPropertiesConfig.expireAccessToken.toInt() * 60
        return AuthResponse(
            accessToken = authJwt.accessToken,
            refreshToken = authJwt.refreshToken,
            scope = authenticateDetail.scope,
            expiresIn = tokenExpireTime,
            tokenType = TokenServiceImpl.TOKEN_TYPE
        )
    }

    fun refreshToken(accessToken: String, clientSecret: String): AuthResponse {
        try {
            val claims = tokenService.decodeAccessToken(accessToken)
            val email = claims[TokenServiceImpl.CLAIM_EMAIL].toString()
            val userId = claims[TokenServiceImpl.CLAIM_USER_ID].toString()
            val scope = claims[TokenServiceImpl.CLAIM_SCOPE].toString()
            logger.info("Generate token userId: $userId, scope: $scope")
            val authJwt =
                tokenService.generateAuthToken(
                    tokenId = claims.id,
                    email = email,
                    userId = userId,
                    clientId = claims.audience,
                    scopes = scope
                )
            val tokenExpireTime = appPropertiesConfig.expireAccessToken.toInt() * 60
            return AuthResponse(
                accessToken = authJwt.accessToken,
                refreshToken = authJwt.refreshToken,
                scope = scope,
                expiresIn = tokenExpireTime,
                tokenType = TokenServiceImpl.TOKEN_TYPE
            )
        } catch (ex: Exception) {
            logger.error("Cannot decode access token error", ex)
            throw ex
        }
    }

    fun generateOauthAccessToken(
        tokenId: String,
        email: String,
        userId: String,
        clientId: String,
        scopes: String
    ): AuthResponse {
        val accessToken = tokenService.generateAccessToken(
            tokenId = tokenId,
            email = email,
            userId = userId,
            clientId = clientId,
            scopes = scopes
        )
        val tokenExpireTime = appPropertiesConfig.expireAccessToken.toInt() * 60
        return AuthResponse(
            accessToken = accessToken,
            scope = scopes,
            expiresIn = tokenExpireTime,
            tokenType = TokenServiceImpl.TOKEN_TYPE
        )
    }

    fun getAuthenticateDetail(code: String): Optional<AuthenticateDetail> {
        val key = buildAuthCodeKey(code)
        val data = redisUtil.getFromRedis(key)
        val convertData = objectMapper.convertValue(data, AuthenticateDetail::class.java)
        return Optional.ofNullable(convertData)
    }

    fun buildAuthCodeKey(code: String): String {
        return "${RedisConstants.AUTH_CODE}:$code"
    }

    fun generateAuthCode(clientId: String, userId: String, email: String, scope: String, redirectUri: String): String {
        val client = getClientIsEnable(clientId)
        val code = UUID.randomUUID().toString().replace("-", "")
        val key = buildAuthCodeKey(code)
        val data = AuthenticateDetail(
            code = code,
            clientName = client.name,
            clientId = client.id,
            clientSecret = client.secret!!,
            userId = userId,
            email = email,
            redirectUri = redirectUri,
            scope = scope
        )
        val expiredAuthCode = appPropertiesConfig.expireAuthCode.toLong() * 60
        redisUtil.putToRedisWithExpire(key, data, expiredAuthCode)
        return code
    }

    fun getValidateUserMockMode(
        username: String,
        encryptPassword: String
    ): UserPoolBaseResponse<ValidateUserResponse> {
        val result = ValidateUserResponse(
            email = username,
            userId = 1,
            externalCustomerId = "1234-1234-1234-1234"
        )
        return UserPoolBaseResponse(
            code = UserPoolCode.USER_NOT_FOUND.code,
            message = UserPoolCode.USER_NOT_FOUND.message,
            result = result
        )
    }

    fun isInScope(scopes: List<String>, scopeSet: Set<String>): Boolean {
        var isInScope = true
        scopes.forEach {
            if (!scopeSet.contains(it)) {
                logger.info("Scope $it not exist")
                isInScope = false
            }
        }
        return isInScope
    }

    fun parseUrl(uri: String): URIBuilder {
        try {
            return URIBuilder("")
        } catch (_: Exception) {
            throw InvalidRequestException("URI is invalid")
        }
    }

}