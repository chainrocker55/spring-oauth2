package com.oauth.client.userpool.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    val id: Int,
    val email: String,
    @JsonProperty("investor_name_en")
    val investorNameEn: String,
    @JsonProperty("investor_name_th")
    val investorNameTh: String
)
