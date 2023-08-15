package com.oauth.service

import com.oauth.client.userpool.model.response.ValidateUserResponse
import com.oauth.data.entity.LoginHistoryEntity
import com.oauth.data.enum.LoginStatus
import com.oauth.data.enum.LoginType
import com.oauth.data.repository.LoginHistoryRepository
import com.oauth.util.UserAgentUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class LoginHistoryService(
    private val loginHistoryRepository: LoginHistoryRepository,
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun saveLoginHistoryAsyncAndIgnoreError(loginHistory: LoginHistoryEntity) {
        CompletableFuture.runAsync {
            try {
                loginHistoryRepository.save(loginHistory)
            } catch (ex: Exception) {
                logger.error("Cannot save login history data: {}", loginHistory, ex)
            }
        }
    }

    fun saveLoginHistoryAsyncAndIgnoreError(
        user: ValidateUserResponse,
        userAgentUtil: UserAgentUtil,
        errorMsg: String? = null,
        sourceIp: String,
        status: LoginStatus,
        loginType: LoginType
    ) {
        saveLoginHistoryAsyncAndIgnoreError(
            LoginHistoryEntity(
                email = user.email,
                userId = user.userId,
                externalCustomerId = user.externalCustomerId,
                sourceIp = sourceIp,
                browser = userAgentUtil.getBrowser(),
                browserVersion = userAgentUtil.getBrowserVersion(),
                deviceType = userAgentUtil.getDeviceType(),
                deviceOs = userAgentUtil.getDeviceOS(),
                isMobile = userAgentUtil.isMobile(),
                loginStatus = status,
                loginType = loginType,
                clientId = "",
                errorMessage = errorMsg,
                rawData = userAgentUtil.getUserAgentString()
            )
        )
    }
}
