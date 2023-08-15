package com.oauth.exception

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

class InvalidDataException(message: String? = null) : RuntimeException(message), BaseException {
    override val errorCode: String = StatusCode.IV9999.code
    override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    override fun getCodes(): Array<String> = arrayOf(StatusCode.IV9999.message)
}