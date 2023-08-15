package com.oauth.exception

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

class UnAuthenticatedException(message: String? = null, code: String = StatusCode.IV7103.code) : RuntimeException(message),
    BaseException {
    override val errorCode: String = code
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED
    override fun getCodes(): Array<String> = arrayOf(StatusCode.IV7103.message)
}