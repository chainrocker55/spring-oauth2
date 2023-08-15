package com.oauth.controller

import com.oauth.client.userpool.const.UserPoolCode
import com.oauth.constant.*
import com.oauth.data.enum.LoginStatus
import com.oauth.data.enum.LoginType
import com.oauth.exception.InvalidRequestException
import com.oauth.exception.UnAuthenticatedException
import com.oauth.model.request.AuthCodeRequest
import com.oauth.model.request.AuthRequest
import com.oauth.model.request.AuthTokenRequest
import com.oauth.model.response.AuthResponse
import com.oauth.model.response.BaseResponse
import com.oauth.service.LoginHistoryService
import com.oauth.service.OauthService
import com.oauth.service.TokenService
import com.oauth.service.impl.TokenServiceImpl
import com.oauth.util.UserAgentUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.apache.http.client.utils.URIBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/oauth")
class OauthController(
    private val oauthService: OauthService,
    private val loginHistoryService: LoginHistoryService,
    private val tokenService: TokenService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        const val UNKNOWN = "UNKNOWN"
    }


    @GetMapping("/authorize")
    fun authorize(
        @RequestParam(name = ParamConst.RESPONSE_TYPE, required = true) responseType: String,
        @RequestParam(name = ParamConst.CLIENT_ID, required = true) clientId: String,
        @RequestParam(name = ParamConst.REDIRECT_URI, required = true) redirectUri: String,
        @RequestParam(name = ParamConst.SCOPE, required = false, defaultValue = ScopeConst.GENERAL) scope: String,
        @RequestParam(name = ParamConst.STATE, required = false) state: String,
        httpReq: HttpServletRequest,
        httpRes: HttpServletResponse
    ): BaseResponse<Any> {
        logger.info("Authorizing response_type: $responseType, client_id: $clientId, redirect_uri: $redirectUri, scope: $scope, state: $state")
        val query = httpReq.queryString
        val scopes = scope.split(",")

        when (responseType) {
            OauthGrantType.CODE -> {
                val loginUrl = oauthService.authorizeCodeFlow(clientId, redirectUri, scopes, state, query)
                val url = URIBuilder(loginUrl).build().toURL().toString()
                httpRes.sendRedirect(url)
            }

            OauthGrantType.TOKEN -> {
                //Not support
            }

            else -> {
                logger.error("Response type is not support: $responseType of client_id: $clientId")
                throw InvalidRequestException("Response type is not support")
            }
        }
        return BaseResponse.success(data = redirectUri)

    }

    @GetMapping("/token")
    fun exchangeCode(
        @RequestParam(name = ParamConst.GRANT_TYPE, required = true) grantType: String,
        @RequestParam(name = ParamConst.CLIENT_ID, required = true) clientId: String,
        @RequestParam(name = ParamConst.CLIENT_SECRET, required = false) clientSecret: String? = null,
        @RequestParam(name = ParamConst.REDIRECT_URI, required = true) redirectUri: String,
        @RequestParam(name = ParamConst.CODE, required = false) code: String,
        httpRes: HttpServletResponse
    ): BaseResponse<Any> {
        logger.info("Authorizing grant_type: $grantType, client_id: $clientId, redirect_uri: $redirectUri")

        when (grantType) {
            OauthGrantType.AUTHORIZATION_CODE -> {
                val response = oauthService.exchangeCode(
                    clientId = clientId, clientSecret = clientSecret!!, redirectUri = redirectUri, code = code
                )
                return BaseResponse.success(response)
            }

            OauthGrantType.CLIENT_CREDENTIALS -> {
                //Not support
            }

            OauthGrantType.REFRESH_TOKEN -> {
                //Not support
            }

            OauthGrantType.PASSWORD -> {
                //Not support
            }

            OauthGrantType.DEVICE_CODE -> {
                //Not support
            }

            else -> {
                logger.error("Grant type is not support: $grantType of client_id: $clientId")
                throw InvalidRequestException("Grant type is not support")
            }
        }
        return BaseResponse.success()

    }

    @PostMapping("/create/auth-code")
    fun generateAuthCode(
        @Valid @RequestBody req: AuthCodeRequest,
        httpRes: HttpServletResponse
    ): BaseResponse<Any> {
        logger.info("Generate auth_code of client_id: ${req.clientId}, redirect_uri: ${req.redirectUrl}")
        val code = oauthService.generateAuthCode(
            clientId = req.clientId,
            userId = req.userId,
            email = req.email,
            scope = req.scope,
            redirectUri = req.redirectUrl,
        )
        val url =
            URIBuilder(req.redirectUrl).addParameter(ParamConst.STATE, req.state).addParameter(ParamConst.CODE, code)
                .build()
                .toURL().toString()
        httpRes.sendRedirect(url)
        logger.info("Generate auth_code of client_id: ${req.clientId} success redirect to: $url")
        return BaseResponse.success(data = url)

    }

    @PostMapping("/create/token")
    fun createToken(
        @Valid @RequestBody req: AuthTokenRequest,
    ): BaseResponse<AuthResponse> {
        logger.info("Generate token of user: ${req.userId}, client_id: ${req.clientId}")
        val id = UUID.randomUUID().toString()
        val response = oauthService.generateOauthAccessToken(
            tokenId = id,
            email = req.email,
            userId = req.userId,
            clientId = req.clientId,
            scopes = req.scope
        )
        logger.info("Generate token of user: ${req.userId}, client_id: ${req.clientId} success")
        return BaseResponse.success(data = response)

    }

    @PostMapping("/authenticate")
    fun authenticate(
        @Valid @RequestBody req: AuthRequest, httpReq: HttpServletRequest, httpRes: HttpServletResponse
    ): BaseResponse<AuthResponse> {
        logger.info("Login user ${req.username} client_id: ${req.clientId}")
        val sourceIp = httpReq.getHeader(HeaderConst.X_FORWARDED_FOR) ?: UNKNOWN
        val userAgent = httpReq.getHeader(HeaderConst.USER_AGENT)
        logger.info("User agent: {}", userAgent.toString())

        if (!oauthService.checkClient(req.clientId)) {
            logger.error("Client is invalid client_id: ${req.clientId}")
            throw UnAuthenticatedException("Client is invalid client_id: ${req.clientId}")
        }
        val userAgentUtil = UserAgentUtil(userAgent)
        val validateUser = oauthService.validateUser(req)
        if (validateUser.code == UserPoolCode.SUCCESS.code) {
            logger.info("Found user: {}", req.username)
            val user = validateUser.result!!
            val id = UUID.randomUUID().toString()
            val accessToken = tokenService.generateAccessToken(
                tokenId = id,
                email = user.email,
                userId = user.userId.toString(),
                clientId = req.clientId,
                scopes = ScopeConst.OTP
            )

            //Save step 1 login history for success case
            loginHistoryService.saveLoginHistoryAsyncAndIgnoreError(
                user = user,
                status = LoginStatus.SUCCESS,
                loginType = LoginType.USER,
                userAgentUtil = userAgentUtil,
                sourceIp = sourceIp
            )
            return BaseResponse.success(
                AuthResponse(
                    accessToken = accessToken,
                    tokenType = TokenServiceImpl.TOKEN_TYPE,
                )
            )

        } else {
            logger.info("Authenticate user is not pass because: {}", validateUser.message)
            //Save step 1 login history for failed case
            if (validateUser.code != UserPoolCode.USER_NOT_FOUND.code) {
                //Save step 1 login history for success case
                loginHistoryService.saveLoginHistoryAsyncAndIgnoreError(
                    user = validateUser.result!!,
                    status = LoginStatus.SUCCESS,
                    loginType = LoginType.USER,
                    userAgentUtil = userAgentUtil,
                    sourceIp = sourceIp
                )
            }
            when (validateUser.code) {
                UserPoolCode.USER_NOT_FOUND.code, UserPoolCode.INVALID_PASSWORD.code -> {
                    throw UnAuthenticatedException(code = StatusCode.IV4142.code, message = StatusCode.IV4142.message)
                }

                UserPoolCode.USER_DISABLED.code -> {
                    throw UnAuthenticatedException(code = StatusCode.IV4141.code, message = StatusCode.IV4141.message)
                }

                UserPoolCode.LOCKED_PASSWORD.code -> {
                    throw UnAuthenticatedException(code = StatusCode.IV4144.code, message = StatusCode.IV4144.message)
                }

                else -> {
                    throw UnAuthenticatedException(code = validateUser.code, message = validateUser.message)
                }
            }

        }
    }

}