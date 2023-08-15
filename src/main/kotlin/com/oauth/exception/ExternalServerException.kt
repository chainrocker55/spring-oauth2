package com.oauth.exception

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

class ExternalServerException(message: String? = null, code: String = StatusCode.IV9999.code) : RuntimeException(message),
    BaseException {
    override val errorCode: String = code
    override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    override fun getCodes(): Array<String> = arrayOf(StatusCode.IV9999.message)
}