package com.oauth.util


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

class UserAgentUtil(userAgentStr: String) {

    private val userAgentString: String = userAgentStr

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        val CRIOS_REGEX: Pattern = Pattern.compile("(?:CriOS|CrMo)\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?(\\.[\\w]+)?)")
        val EDG_REGEX: Pattern =
            Pattern.compile("(?:EdgA|Edg|Edge|EdgiOS)\\/(([0-9]+)\\.?([\\w]+)?(\\.[\\w]+)?(\\.[\\w]+)?)")
    }

    fun getBrowser(): String {
        val isEdge = userAgentString.contains("Edg")
        return if (isEdge) "Edge" else "Browser Name"
    }

    fun getBrowserVersion(): String {
       return "Version"
    }


    fun getDeviceOS(): String? {
        return  "DeviceOs"
    }

    fun getDeviceType(): String {
        return "DeviceType"
    }

    fun isMobile(): Boolean {
        return false
    }

    fun getUserAgentString(): String {
        return this.userAgentString
    }
}
