package com.oauth.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisUtil(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }


    fun putToRedisWithExpire(key: String, data: Any, duration: Long) {
        try {
            redisTemplate.opsForValue().set(key, data)
            redisTemplate.expire(key, Duration.ofSeconds(duration))
        } catch (e: Exception) {
            logger.info("error write value from redis : $e")
        }
    }

    fun putToRedis(key: String, data: Any) {
        try {
            redisTemplate.opsForValue().set(key, data)
        } catch (e: Exception) {
            logger.info("error write value from redis : $e")
        }
    }

    fun getFromRedis(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }


    fun deleteKey(key: String) {
        try {
            val isKeyExisted = redisTemplate.hasKey(key)
            if (isKeyExisted) {
                redisTemplate.delete(key)
            }
        } catch (e: Exception) {
            logger.info("error delete key from redis : $e")
        }
    }

    fun getRedisTTL(key: String): Long? {
        return redisTemplate.getExpire(key)
    }
}