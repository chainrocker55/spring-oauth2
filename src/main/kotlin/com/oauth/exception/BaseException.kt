package com.oauth.exception

import org.springframework.context.MessageSourceResolvable
import org.springframework.http.HttpStatus

interface BaseException : MessageSourceResolvable {
    val errorCode: String
    val httpStatus: HttpStatus
}