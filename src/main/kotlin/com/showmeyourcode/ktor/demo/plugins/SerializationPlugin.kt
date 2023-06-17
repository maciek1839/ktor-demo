package com.showmeyourcode.ktor.demo.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun Application.configureSerialization() {
    log.info("Initializing JSON serialization...")
    install(ContentNegotiation) {
        json()
    }
}
