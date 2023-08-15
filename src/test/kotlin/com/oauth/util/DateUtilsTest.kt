package com.oauth.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalTime

@ExtendWith(SpringExtension::class)
@ActiveProfiles(profiles = ["test"])
class DateUtilsTest{
    @Test
    fun `test time is in rang from target string`(){
        val target = "01:30:00"
        val start = "01:00:00"
        val end = "02:00:00"
        val result = DateUtils.isBetweenTimeStr(start, end , target)
        Assertions.assertTrue(result)
    }
    @Test
    fun `test time is not in rang from target string`(){
        val target = "02:00:01"
        val start = "01:00:00"
        val end = "02:00:00"
        val result = DateUtils.isBetweenTimeStr(start, end , target)
        Assertions.assertFalse(result)
        val target2 = "00:30:00"
        val result2 = DateUtils.isBetweenTimeStr(start, end , target2)
        Assertions.assertFalse(result2)
    }

    @Test
    fun `test time is in rang from target localtime`(){
        val target = LocalTime.parse("00:00:01")
        val start = "00:00:00"
        val end = "01:00:00"
        val result = DateUtils.isBetweenTimeStr(start, end , target)
        Assertions.assertTrue(result)
    }

    @Test
    fun `test time is not in rang from target localtime`(){
        val target = LocalTime.parse("02:00:01")
        val start = "01:00:00"
        val end = "02:00:00"
        val result = DateUtils.isBetweenTimeStr(start, end , target)
        Assertions.assertFalse(result)
        val target2 = LocalTime.parse("00:00:01")
        val result2 = DateUtils.isBetweenTimeStr(start, end , target2)
        Assertions.assertFalse(result2)
    }

}