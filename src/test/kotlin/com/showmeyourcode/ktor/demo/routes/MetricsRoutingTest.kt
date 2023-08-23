package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.SERVICE_NAME_HEADER
import com.showmeyourcode.ktor.demo.plugins.SERVICE_NAME_VALUE
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MetricsRoutingTest {

    @Test
    fun `should call metrics endpoint`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get(RoutingConstant.METRICS).apply {
                val response = call.response

                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals("text/plain; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertEquals(SERVICE_NAME_VALUE, response.headers[SERVICE_NAME_HEADER])
                assertNotNull(response.bodyAsText())
            }
        }
    }

    @Test
    fun `should all health endpoint`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }
            client.get(RoutingConstant.HEALTH).apply {
                val response = call.response

                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertEquals(SERVICE_NAME_VALUE, response.headers[SERVICE_NAME_HEADER])
                assertNotNull(response.bodyAsText())
            }
        }
    }
}
