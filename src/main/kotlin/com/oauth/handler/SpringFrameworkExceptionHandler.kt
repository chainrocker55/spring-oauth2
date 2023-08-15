package com.oauth.handler

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.oauth.constant.StatusCode
import com.oauth.model.response.BaseResponse
import com.oauth.exception.*
import org.springframework.context.MessageSource
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.text.MessageFormat

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class SpringFrameworkExceptionHandler(val messageSource: MessageSource) {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): BaseResponse<Nothing> {
        val msg = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.defaultMessage}" }
        return BaseResponse.error(StatusCode.IV6001.code, msg)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): BaseResponse<Nothing> {
        val type = ex.requiredType!!
        val message: String
        val errorCode: String
        if (type.isEnum) {
            val enumList = type.enumConstants.joinToString(", ") { "$it" }
            errorCode = StatusCode.IV4123.code
            message = MessageFormat.format(StatusCode.IV4123.code, ex.name, enumList)
        } else {
            errorCode = StatusCode.IV4124.code
            message = MessageFormat.format(StatusCode.IV4124.code, ex.name, type.typeName)
        }
        return BaseResponse.error(errorCode, message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(ex: HttpMessageNotReadableException): BaseResponse<Nothing> {
        val clause = ex.mostSpecificCause
        val errorCode: String
        val message: String
        if (clause is InvalidFormatException) {
            val type = clause.targetType
            val fieldName = getInvalidFormatExceptionFieldName(clause)
            if (type.isEnum) {
                val enumList = type.enumConstants.joinToString(", ") { "$it" }
                errorCode = StatusCode.IV4123.code
                message = MessageFormat.format(StatusCode.IV4123.code, fieldName, enumList)
            } else {
                errorCode = StatusCode.IV4124.code
                message = MessageFormat.format(StatusCode.IV4124.code, fieldName, type.typeName)
            }
        } else if (clause is MissingKotlinParameterException) {
            errorCode = StatusCode.IV4122.code
            message = StatusCode.IV4122.message
        } else {
            errorCode = StatusCode.IV6001.code
            message = StatusCode.IV6001.message
        }
        return BaseResponse.error(errorCode, message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentException(ex: IllegalArgumentException): BaseResponse<Nothing> {
        return BaseResponse.error(StatusCode.IV6001)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(InvalidRequestException::class)
    fun invalidRequestException(ex: InvalidRequestException): BaseResponse<Nothing> {
        return BaseResponse.error(ex.errorCode, ex.message ?: StatusCode.IV6001.message)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(DataNotFoundException::class)
    fun dataNotFoundException(ex: DataNotFoundException): BaseResponse<Nothing> {
        return BaseResponse.error(ex.errorCode, ex.message ?: StatusCode.IV4103.message)
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(UnAuthenticatedException::class)
    fun unauthorizedException(ex: UnAuthenticatedException): BaseResponse<Nothing> {
        return BaseResponse.error(ex.errorCode, ex.message ?: StatusCode.IV7103.message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(InternalServerException::class)
    fun internalServerException(ex: InternalServerException): BaseResponse<Nothing> {
        return BaseResponse.error(ex.errorCode, ex.message ?: StatusCode.IV9100.message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(InternalServerError::class)
    fun internalServerError(ex: InternalServerError): BaseResponse<Nothing> {
        return BaseResponse.error(StatusCode.IV9100)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(ExternalServerException::class)
    fun externalException(ex: ExternalServerException): BaseResponse<Nothing> {
        return BaseResponse.error(ex.errorCode, ex.message ?: StatusCode.IV9999.message)
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(ex: MissingServletRequestParameterException): BaseResponse<Nothing> {
        return BaseResponse.error(StatusCode.IV6001)
    }


    private fun getInvalidFormatExceptionFieldName(ex: InvalidFormatException): String? {
        for (r in ex.path) return r.fieldName
        return null
    }

}
