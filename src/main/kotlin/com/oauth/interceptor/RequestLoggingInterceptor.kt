package com.oauth.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*

@Component
class RequestLoggingInterceptor(): HandlerInterceptor {

    companion object {
        const val SESSION_CORRELATION_ID = "correlation_id"
        private const val PRINCIPAL_ID = "principalId"
        private val LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor::class.java)
    }

    fun getCorrelationId(request: HttpServletRequest): String {
        val correlationId = request.getHeader(SESSION_CORRELATION_ID)
            ?: "${UUID.randomUUID()}"
        MDC.put(SESSION_CORRELATION_ID, correlationId)
        return correlationId
    }

    fun setUpPrincipal(request: HttpServletRequest) {
        // email
        val email = request.getHeader(PRINCIPAL_ID)
        if (email.isNullOrEmpty()) {
            MDC.put(PRINCIPAL_ID, "-")
        } else {
            MDC.put(PRINCIPAL_ID, email)
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val logA = parseLog(request)
        MDC.put(SESSION_CORRELATION_ID, getCorrelationId(request))
        setUpPrincipal(request)
        LOGGER.info(logA)
        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        MDC.remove(SESSION_CORRELATION_ID)
    }

    /**
     * JSON output for requestLog
     */
    private fun parseLog(request: HttpServletRequest): String {
        val c = getCorrelationId(request)
        val method = request.method
        val remoteAddress = request.remoteAddr
        val path = request.requestURI
        return "[$c] $method $path ($remoteAddress)"
    }
}