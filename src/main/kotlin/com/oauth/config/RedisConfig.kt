package com.oauth.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RedisConfig::class.java)
    }

    @Value("\${invx.redis.host}")
    lateinit var host: String

    @Value("\${invx.redis.port}")
    private val port = 0

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        logger.info("redis use standalone mode : $host, $port")
        val configuration = RedisStandaloneConfiguration(host, port)
        val jedisConnectionFactory = JedisConnectionFactory(configuration)
        jedisConnectionFactory.afterPropertiesSet()
        return jedisConnectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = jedisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}
