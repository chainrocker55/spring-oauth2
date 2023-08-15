package com.oauth.client.keymanagement

import com.oauth.client.keymanagement.config.KMSSSLVerifyConfig
import com.oauth.client.keymanagement.response.AuthCryptoInfoFile
import com.oauth.client.keymanagement.response.KmsKeyPair
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@FeignClient(
    value = "kms",
    url = "\${kms.url}",
    configuration = [KMSSSLVerifyConfig::class],
)
interface KMSApiService {

    @GetMapping("/api/v1/file/{filename}")
    fun getCryptoFile(@PathVariable(value = "filename") fileName: String): AuthCryptoInfoFile
}