package com.oauth.config

import com.oauth.interceptor.RequestLoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.*


@Configuration
@EnableWebMvc
@Profile("!test")
class WebMvcConfig(
    val requestLoggingInterceptor: RequestLoggingInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestLoggingInterceptor)
    }
}
