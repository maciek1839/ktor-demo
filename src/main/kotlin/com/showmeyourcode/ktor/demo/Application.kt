package com.showmeyourcode.ktor.demo

import com.showmeyourcode.ktor.demo.auth.JwtService
import com.showmeyourcode.ktor.demo.auth.hashFunction
import com.showmeyourcode.ktor.demo.plugins.*
import com.showmeyourcode.ktor.demo.routes.*
import com.showmeyourcode.ktor.demo.user.UserService
import io.ktor.server.application.*

val jwtService = JwtService()
val userService = UserService()

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureFlyway()
    configureSecurity()
    configureHttp()
    configureApiRouting(userService, jwtService, hashFunction)
    configureLoginRouting(userService, jwtService, hashFunction)
    configureMonitoring()
}
