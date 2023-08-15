package com.oauth.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.oauth.constant.StatusCode
import com.oauth.model.Meta

@JsonInclude(JsonInclude.Include.NON_NULL)
class BaseResponse<T>(
    var code: String,
    var message: String,
    var data: T? = null,
    var cause: String? = null,
    val meta: Meta? = null
) {

    constructor(statusCode: StatusCode, data: T?) : this(
        statusCode.code,
        statusCode.message,
        data
    )

    class Builder<T> {
        fun success(data: T? = null): BaseResponse<T> {
            return BaseResponse(StatusCode.SUCCESS, data)
        }

        fun error(code: String, message: String, data: T? = null, cause: String? = null): BaseResponse<T> {
            return BaseResponse(code, message, data, cause)
        }
        fun error(statusCode: StatusCode, data: T? = null, cause: String? = null): BaseResponse<T> {
            return BaseResponse(statusCode.code, statusCode.message, data, cause)
        }
    }

    companion object {
        fun <T> success(data: T? = null, meta: Meta? = null): BaseResponse<T> {
            return BaseResponse(StatusCode.SUCCESS.code, StatusCode.SUCCESS.message, data, meta = meta)
        }
        fun <T> success(statusCode: StatusCode, data: T? = null, meta: Meta? = null): BaseResponse<T> {
            return BaseResponse(statusCode.code, statusCode.message, data, meta = meta)
        }

        fun <T> error(statusCode: StatusCode, data: T? = null, cause: String? = null): BaseResponse<T> {
            return BaseResponse(statusCode.code, statusCode.message, data, cause)
        }

        fun <T> error(code: String, message: String, data: T? = null, cause: String? = null): BaseResponse<T> {
            return BaseResponse(code, message, data, cause)
        }
    }
}
