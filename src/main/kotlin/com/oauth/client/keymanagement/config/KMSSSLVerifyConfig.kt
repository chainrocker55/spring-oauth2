package com.oauth.client.keymanagement.config

import com.oauth.client.keymanagement.config.KMSConfig
import feign.Client
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory


class KMSSSLVerifyConfig {
    @Bean
    fun feignClient(kmsConfig: KMSConfig): Client {
        return Client.Default(getSSLSocketFactory(kmsConfig), NoopHostnameVerifier())
    }
    private fun getSSLSocketFactory(kmsConfig: KMSConfig): SSLSocketFactory? {
        try {
            val acceptingTrustStrategy = TrustStrategy { _, _ -> kmsConfig.allowAllCerts }
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