package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    log.info("Initializing logging and metrics...")
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith(RoutingConstant.DEFAULT) }
    }

    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }

    routing {
        get(RoutingConstant.METRICS) {
            call.respond(appMicrometerRegistry.scrape())
        }
        get(RoutingConstant.HEALTH) {
            call.respond(mapOf("health" to true))
        }
    }
}
