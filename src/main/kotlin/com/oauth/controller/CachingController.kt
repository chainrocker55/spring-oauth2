package com.oauth.controller

import com.oauth.model.response.BaseResponse
import com.oauth.service.CachingService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/caching")
class CachingController(
    val cachingService: CachingService,
) {

    @PutMapping("/evict/all")
    fun evictAllCache(
    ): BaseResponse<Any> {
        cachingService.evictAllCacheValues()
        return BaseResponse.success()
    }
    @PutMapping("/evict/{name}")
    fun evictCacheByName(
        @PathVariable("name") name: String
    ): BaseResponse<Any> {
        cachingService.evictSingleCacheValueByName(name)
        return BaseResponse.success()
    }


}