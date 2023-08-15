package com.oauth.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@Order(1)
class RequestResponseFilter: OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            MDC.put("sourceIp", getClientIp(request))
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        var remoteAddress = request.getHeader("X-SOURCE-IP")
        if (remoteAddress == null || "" == remoteAddress) {
            remoteAddress = ""
        }
        return remoteAddress
    }
}