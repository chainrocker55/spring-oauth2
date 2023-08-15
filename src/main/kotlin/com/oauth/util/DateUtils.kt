package com.oauth.util

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

object DateUtils {
    const val DATE_PATTERN: String = "dd-MM-yyyy"
    const val TIME_PATTERN: String = "HH:mm:ss"
    const val ISO8601: String = "yyyy-MM-dd'T'HH:mm:ssXXX"
    fun getNextMinute(next: Int): Date {
        val curDate = Calendar.getInstance()
        curDate.add(Calendar.MINUTE, next)
        return Date.from(curDate.toInstant())
    }

    fun getLastMinutes(next: Int): Date {
        val curDate = Calendar.getInstance()
        curDate.add(Calendar.MINUTE, -next)
        return Date.from(curDate.toInstant())
    }


    fun getNextHour(next: Int): Date {
        val curDate = Calendar.getInstance()
        curDate.add(Calendar.HOUR, next)
        return Date.from(curDate.toInstant())
    }

    fun getLastHour(next: Int): Date {
        val curDate = Calendar.getInstance()
        curDate.add(Calendar.HOUR, -next)
        return Date.from(curDate.toInstant())
    }

    fun getLastDate(next: Int): Date {
        val curDate = Calendar.getInstance()
        curDate.add(Calendar.DATE, -next)
        return Date.from(curDate.toInstant())
    }

    fun formatDateToString(d: Date = Date(), pattern: String = DATE_PATTERN): String {
        return SimpleDateFormat(pattern).format(d)
    }

    // start or end parameter should be HH:mm:ss pattern
    fun isBetweenTimeStr(start: String, end: String, target: LocalTime = LocalTime.now()): Boolean {
        return target.isAfter(LocalTime.parse(start)) && target.isBefore(LocalTime.parse(end))
    }

    fun isBetweenTimeStr(start: String, end: String, target: String): Boolean {
        return isBetweenTimeStr(start, end, LocalTime.parse(target))
    }

}
