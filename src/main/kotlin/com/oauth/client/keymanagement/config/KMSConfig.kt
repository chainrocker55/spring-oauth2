package com.oauth.client.keymanagement.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "key-management")
class KMSConfig {
    lateinit var url: String
    var allowAllCerts: Boolean = true
    var mock: Boolean = false
    lateinit var passphraseKeyFilename: String
    lateinit var accessTokenKeyFilename: String
    lateinit var refreshTokenKeyFilename: String
}