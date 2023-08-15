package com.oauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OauthApplication

fun main(args: Array<String>) {
	runApplication<OauthApplication>(*args)
}
