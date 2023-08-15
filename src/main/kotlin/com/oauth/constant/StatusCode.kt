package com.oauth.constant

import lombok.Getter

@Getter
enum class StatusCode(val code: String, val message: String) {
    SUCCESS("0000", "SUCCESS"),
    IV4103("4103", "Data not found"),
    IV4122("4122", "Parameter is missing"),
    IV4123("4123", "The parameter {0} must have a value among : {1}"),
    IV4124("4124", "The parameter {0} must be of type {1}"),
    IV4141("4141", "User is disabled"),
    IV4142("4142", "User not found or password is invalid"),
    IV4144("4144", "User password is locked"),
    IV6001("6001", "Request Invalid."),
    IV7103("7103", "Authorization invalid"),
    IV7104("7104", "Authorization expire"),
    IV9100("9100", "Internal Server Error"),
    IV9999("9999", "External Server Error"),
}
