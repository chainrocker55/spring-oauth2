package com.oauth.exception;

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import org.springframework.http.HttpStatus

open class ErrorCodeException(val errorCustom: StatusCode, vararg val params: Any = emptyArray(), message: String? = null) : RuntimeException(message),
    BaseException {
    override val errorCode: String = errorCustom.code
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
    override fun getCodes(): Array<String> = arrayOf()
    override fun getArguments(): Array<Any> = arrayOf(params)
}
