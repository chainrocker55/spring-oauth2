package com.oauth.client.userpool.config

import com.invx.oauth.client.admintool.config.UserPoolConfig
import feign.RequestInterceptor
import feign.RequestTemplate

class UserPoolRequestInterceptor(val config: UserPoolConfig) : RequestInterceptor {

    companion object {
        const val API_KEY = "x-api-key"
        const val APP_ID = "x-app-id"
    }

    override fun apply(template: RequestTemplate) {
        template.header(APP_ID, config.appId)
        template.header(API_KEY, config.apiKey)

    }
}