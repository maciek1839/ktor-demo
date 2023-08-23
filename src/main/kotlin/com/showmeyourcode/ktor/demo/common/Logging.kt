package com.showmeyourcode.ktor.demo.common

import io.ktor.server.application.*
import io.ktor.server.request.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
