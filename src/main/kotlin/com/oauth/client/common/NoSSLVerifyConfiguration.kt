package com.oauth.client.common

import feign.Client
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory


class NoSSLVerifyConfiguration {
    @Bean
    fun feignClient(): Client {
        return Client.Default(getSSLSocketFactory(), NoopHostnameVerifier())
    }

    private fun getSSLSocketFactory(): SSLSocketFactory? {
        try {
            val acceptingTrustStrategy = TrustStrategy { _, _ -> true }
            val sslContext: SSLContext = SSLContexts
                .custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build()
            return sslContext.socketFactory
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return null
    }
}