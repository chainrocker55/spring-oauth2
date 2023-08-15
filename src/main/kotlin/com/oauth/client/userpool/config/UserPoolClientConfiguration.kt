package com.oauth.client.userpool.config

import com.invx.oauth.client.admintool.config.UserPoolConfig
import feign.Client
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

class UserPoolClientConfiguration(
    private val config: UserPoolConfig
) {
//    @Bean
//    fun createWebAuthRequestInterceptor(): RequestInterceptor {
//        return AdminToolRequestInterceptor(config)
//    }

    @Bean
    fun feignClient(): Client {
        return Client.Default(getSSLSocketFactory(), NoopHostnameVerifier())
    }

    private fun getSSLSocketFactory(): SSLSocketFactory? {
        try {
            if (config.allowAllCerts) {
                val acceptingTrustStrategy = TrustStrategy { _, _ -> config.allowAllCerts }
                val sslContext: SSLContext = SSLContexts
                    .custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build()
                return sslContext.socketFactory
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return null
    }
}