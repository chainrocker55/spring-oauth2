package com.oauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app-properties")
class AppPropertiesConfig {
    lateinit var expireAccessToken: String
    lateinit var expireRefreshToken: String
    lateinit var expireAuthCode: String
    lateinit var iss: String
    lateinit var loginUrl: String
}