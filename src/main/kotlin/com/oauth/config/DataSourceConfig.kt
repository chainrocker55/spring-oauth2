package com.oauth.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val dbConfig: DataBaseConfig,
    private val dbHikariConfig: DataBaseHikariConfig,
//    private val kmsService: KMSService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DataSourceConfig::class.java)
        private val ENC_PW_PATTERN = Pattern.compile("ENC\\(([^\\)]+)\\)")
    }

    @Bean
    fun dataSource(): DataSource {
        var password: String = dbHikariConfig.password
        val matcher: Matcher = ENC_PW_PATTERN.matcher(password)
        if (matcher.matches()) {
            logger.info("Decrypting encrypted password for datasource (database connection).")
            val encPassword = matcher.group(1)
//            val cryptoInfo = kmsService.getPassphraseCryptoFile()
//            password = kmsService.decrypt(encPassword, cryptoInfo)
        }

        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = dbConfig.url
        hikariConfig.driverClassName = dbConfig.driverClassName
        hikariConfig.username = dbHikariConfig.username
        hikariConfig.password = password
        hikariConfig.validationTimeout = dbHikariConfig.validationTimeout
        hikariConfig.idleTimeout = dbHikariConfig.idleTimeout
        hikariConfig.maximumPoolSize = dbHikariConfig.maximumPoolSize
        hikariConfig.maxLifetime = dbHikariConfig.maxLifetime
        hikariConfig.connectionTimeout = dbHikariConfig.connectionTimeout
        hikariConfig.connectionTestQuery = dbHikariConfig.connectionTestQuery
        hikariConfig.minimumIdle = dbHikariConfig.minimumIdle
        hikariConfig.leakDetectionThreshold = dbHikariConfig.leakDetectionThreshold
        return HikariDataSource(hikariConfig)
    }
}