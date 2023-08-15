package com.oauth.config

import feign.micrometer.MicrometerCapability
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress

@Configuration
class MetricConfig {
    @Bean
    fun meterRegistryCustomizer(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer<MeterRegistry> { registry ->
            var ipAddress = "unknown"
            var host = "unknown"
            try {
                ipAddress = InetAddress.getLocalHost().hostAddress
                host = InetAddress.getLocalHost().hostName
            } catch (_: Exception) {
            }
            registry.config().commonTags("ip", ipAddress)
            registry.config().commonTags("host", host)
        }
    }

    @Bean
    fun micrometerCapability(meterRegistry: MeterRegistry): MicrometerCapability {
        return MicrometerCapability(meterRegistry)
    }

}