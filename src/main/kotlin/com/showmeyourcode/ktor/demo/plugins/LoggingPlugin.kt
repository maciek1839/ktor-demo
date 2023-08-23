package com.showmeyourcode.ktor.demo.plugins

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith(RoutingConstant.DEFAULT) }
    }
}
