package com.invx.oauth.client.admintool.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "easyinvest-admin-tool")
class UserPoolConfig {
    lateinit var url: String
    lateinit var appId: String
    lateinit var apiKey: String
    var mock: Boolean? = false
    var allowAllCerts: Boolean = true
}