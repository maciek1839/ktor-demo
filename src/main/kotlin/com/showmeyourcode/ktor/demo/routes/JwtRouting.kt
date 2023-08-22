package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureOAuthRouting() {
    log.info("Initializing OAuth routing...")
    routing {
        post(RoutingConstant.OAUTH_AUTHORIZE) {
        }

        post(RoutingConstant.OAUTH) {
        }

        get(RoutingConstant.OAUTH_STATUS) {
        }

        post(RoutingConstant.OAUTH_REVOKE) {
        }
    }
}
