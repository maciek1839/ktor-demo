package com.showmeyourcode.ktor.demo.plugins

import com.showmeyourcode.ktor.demo.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureFlyway() {
    log.info("Initializing Flyway...")
    DatabaseFactory.init()
}
