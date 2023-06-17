package com.showmeyourcode.ktor.demo.plugins

import com.showmeyourcode.ktor.demo.configuration.appConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

const val SERVICE_NAME_HEADER = "X-Service-Name"
const val SERVICE_NAME_VALUE = "User-Management"

fun Application.configureHttp() {
    log.info("Initializing HTTP...")

    install(DefaultHeaders) {
        header(SERVICE_NAME_HEADER, SERVICE_NAME_VALUE)
    }

    install(CORS) {
        allowHost("0.0.0.0:8081")
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        val hosts = appConfig.property("cors.hosts").getList()
        if (hosts.isEmpty()) {
            this@configureHttp.log.warn("No hosts were configured to access the app. Make it any host available.")
            anyHost()
        } else {
            appConfig.property("cors.hosts").getList().forEach {
                this@configureHttp.log.info("Configuring host '$it' to access the app.")
                allowHost(it)
            }
        }
    }
}
