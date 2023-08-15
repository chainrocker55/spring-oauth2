package com.oauth.client.userpool.const

import lombok.Getter

@Getter
enum class UserPoolCode(val code: String, val message: String) {
    SUCCESS("0000", "SUCCESS"),
    USER_NOT_FOUND("4102", "User not found."),
    USER_DISABLED("4141", "User is disabled"),
    INVALID_PASSWORD("4142", "User password is invalid"),
    LOCKED_PASSWORD("4144", "User password is locked"),
    NOT_FOUND_ASSET_CLASS("4145", "User not found asset class"),
    INVALID_PIN("4151", "User pin is invalid");
}
