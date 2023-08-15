package com.oauth.client.common

import feign.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import java.net.InetSocketAddress
import java.net.Proxy

class ProxyConfiguration {

    @Value("\${proxy.host}")
    lateinit var host: String

    @Value("\${proxy.port}")
    lateinit var port: String

    @Bean
    fun clientBuilder(): Client {
        return Client.Proxied(null, null, Proxy(Proxy.Type.HTTP, InetSocketAddress(host, port.toInt())))
    }
}