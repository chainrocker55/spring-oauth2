package com.oauth.handler

import com.oauth.constant.StatusCode
import com.oauth.exception.BaseException
import com.oauth.exception.ErrorCodeException
import com.oauth.model.response.BaseResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.text.MessageFormat
import java.util.*

@ControllerAdvice
class RestResponseEntityExceptionHandler(
) : ResponseEntityExceptionHandler() {

    @Value("\${message.cause.hide}")
    val hideCauseMessage: Boolean? = false

    @ExceptionHandler(Exception::class)
    fun defaultException(ex: Exception, req: WebRequest, locale: Locale): ResponseEntity<BaseResponse<String>> {
        val errorCode = when (ex) {
            is BaseException -> ex.errorCode
            else ->  StatusCode.IV9100.code
        }
        val message = when (ex) {
            is ErrorCodeException -> MessageFormat.format(ex.errorCustom.message, *ex.params)
            else -> StatusCode.IV9100.message
        }
        val httpStatus = when (ex) {
            is ErrorCodeException -> when (ex.errorCustom) {
                StatusCode.IV9100 -> HttpStatus.INTERNAL_SERVER_ERROR
                StatusCode.SUCCESS -> HttpStatus.OK
                else -> HttpStatus.BAD_REQUEST
            }
            is BaseException -> ex.httpStatus
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        logger.error("defaultException: ", ex)
        val response =
            BaseResponse.Builder<String>().error(
                errorCode,
                message,
                null,
                when (hideCauseMessage) {
                    false -> ex.message
                    else -> null
                }
            )
        return ResponseEntity(response, httpStatus)
    }


}
