package com.oauth.service

import org.apache.logging.log4j.LogManager
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service


@Service
class CachingService(val cacheManager: CacheManager) {

    private val logger = LogManager.getLogger(CachingService::class)

    fun evictSingleCacheValueByNameAndKey(name: String, key: String) {
        logger.info("Clear cache name: $name, cache key: $key")
        cacheManager.getCache(name)?.evict(key)
    }

    fun evictSingleCacheValueByName(name: String) {
        logger.info("Clear cache name: $name")
        cacheManager.getCache(name)?.clear()
    }

    fun evictAllCacheValues() {
        logger.info("Clear all cache")
        cacheManager.cacheNames.stream().parallel().forEach { cacheManager.getCache(it)?.clear() }
    }


    //Scheduled
//    @CacheEvict(
//        allEntries = true,
//        value = [CacheName.SEARCH_ENGINE_FILE]
//    )
//    @Scheduled(fixedDelay = ONE_WEEK)
//    fun cryptoFileCacheEvict() {
//        logger.info("Clear cache value of name: ${CacheName.SEARCH_ENGINE_FILE}")
//    }

}