package com.oauth.exception

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

class DataNotFoundException(message: String? = null) : RuntimeException(message),
    BaseException {
    override val errorCode: String = StatusCode.IV4103.code
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
    override fun getCodes(): Array<String> = arrayOf(StatusCode.IV4103.message)
}