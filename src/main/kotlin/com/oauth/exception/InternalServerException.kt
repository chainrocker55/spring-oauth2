package com.oauth.exception

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

class InternalServerException(message: String? = null, code: String = StatusCode.IV9100.code) : RuntimeException(message),
    BaseException {
    override val errorCode: String = code
    override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    override fun getCodes(): Array<String> = arrayOf(StatusCode.IV9100.message)
}