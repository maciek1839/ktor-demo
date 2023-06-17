package com.showmeyourcode.ktor.demo.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

const val BASIC_AUTH_INTERNAL = "basic-auth-internal"

fun Application.configureSecurity() {
    authentication {
        basic(name = BASIC_AUTH_INTERNAL) {
            realm = "ShowMeYourCode Users"
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
