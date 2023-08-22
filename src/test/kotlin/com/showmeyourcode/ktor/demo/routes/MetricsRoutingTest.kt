package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.configureSerialization
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MetricsRoutingTest {
    @Test
    fun `should call metrics endpoint`() {
        withTestApplication({ configureMetrics() }) {
            handleRequest(HttpMethod.Get, RoutingConstant.METRICS).apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should all health endpoint`() {
        withTestApplication({
            configureSerialization()
            configureMetrics()
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.HEALTH).apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }
    }
}
