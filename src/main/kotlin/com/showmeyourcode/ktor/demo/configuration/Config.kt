package com.showmeyourcode.ktor.demo.configuration

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

const val DEFAULT_CONFIG_FILE = "application.conf"

fun getConfigFile(): String {
    val profile = System.getenv("KTOR_PROFILE")
    return if (profile == null) {
        DEFAULT_CONFIG_FILE
    } else {
        "application-$profile.conf"
    }
}

val appConfig = HoconApplicationConfig(ConfigFactory.load(getConfigFile()))
