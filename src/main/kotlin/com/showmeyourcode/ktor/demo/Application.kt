package com.showmeyourcode.ktor.demo

import com.showmeyourcode.ktor.demo.oauth.OAuthService
import com.showmeyourcode.ktor.demo.plugins.*
import com.showmeyourcode.ktor.demo.routes.*
import com.showmeyourcode.ktor.demo.user.UserService
import io.ktor.server.application.*

val oauthService = OAuthService()
val userService = UserService()

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

// application.conf references the main function. This annotation prevents the IDE from marking it as unused.
@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureFlyway()
    configureSecurity()
    configureHttp()
    configureMetrics()
    configureUsersRouting(userService)
    configureOAuthRouting(oauthService)
}
