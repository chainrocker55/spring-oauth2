package com.oauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
class DataBaseConfig {
    lateinit var url: String
    lateinit var driverClassName: String
}

@Configuration
@ConfigurationProperties(prefix = "spring.datasource.hikari")
class DataBaseHikariConfig {
    lateinit var username: String
    lateinit var password: String
    lateinit var connectionTestQuery: String
    var connectionTimeout: Long = 10000
    var validationTimeout: Long = 5000
    var idleTimeout: Long = 600000
    var maxLifetime: Long = 1800000
    var maximumPoolSize: Int = 100
    var minimumIdle: Int = 20
    var leakDetectionThreshold: Long = 0
}