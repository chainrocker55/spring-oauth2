package com.oauth.client.userpool.rest

import com.oauth.client.userpool.config.UserPoolClientConfiguration
import com.invx.oauth.client.admintool.model.request.ValidateUserRequest
import com.oauth.client.userpool.model.response.UserPoolBaseResponse
import com.oauth.client.userpool.model.response.ValidateUserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "adminToolClient",
    url = "\${easyinvest-admin-tool.url}",
    configuration = [UserPoolClientConfiguration::class]
)
interface UserPoolClient {

    @PostMapping(value = ["/api/v1/oauth/auth/user"])
    fun validateUser(@RequestBody req: ValidateUserRequest): UserPoolBaseResponse<ValidateUserResponse>
}