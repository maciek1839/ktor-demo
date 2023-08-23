package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.*
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.*
import com.showmeyourcode.ktor.demo.response.ErrorMessage
import com.showmeyourcode.ktor.demo.response.ServerResponse
import com.showmeyourcode.ktor.demo.user.RegisterUser
import com.showmeyourcode.ktor.demo.user.toJson
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.*

class UsersRoutingTest {

    @AfterTest
    fun afterTest() {
        runBlocking {
            userService.deleteAll()
        }
    }

    @Test
    fun `should create a new user WHEN data are valid`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.post(RoutingConstant.API_USERS) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(validNewUser.toJson())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.Created, response.status)
                assertTrue(response.bodyAsText().isEmpty())
                assertEquals(SERVICE_NAME_VALUE, response.headers[SERVICE_NAME_HEADER])
                assertNotNull(response.headers[HttpHeaders.Location])
            }
        }
    }

    @Test
    fun `should not create a user WHEN another user with the same email already exists`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            runBlocking {
                userService.createUser(validNewUser)
            }.apply {
                client.post(RoutingConstant.API_USERS) {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(validNewUser.toJson())
                }.apply {
                    val response = call.response
                    assertEquals(HttpStatusCode.BadRequest, response.status)
                    assertFalse(response.bodyAsText().isEmpty())
                }
            }
        }
    }

    @Test
    fun `should not create a user WHEN email or password are empty`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.post(RoutingConstant.API_USERS) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser(null, "pswd").toJson())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertTrue(response.bodyAsText().isNotBlank())
            }

            client.post(RoutingConstant.API_USERS) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example@gmail.com", null).toJson())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertTrue(response.bodyAsText().isNotBlank())
            }
        }
    }

    @Test
    fun `should not create a user WHEN an email is invalid`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.post(RoutingConstant.API_USERS) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example.invalid-address", "pswd").toJson())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertTrue(response.bodyAsText().isNotBlank())
            }
        }
    }

    @Test
    fun `should not allow access to users count endpoint WHEN credentials are missing`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get(RoutingConstant.COUNT).apply {
                val response = call.response
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }
    }

    @Test
    fun `should not allow to access to users count endpoint WHEN credentials are bad`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get(RoutingConstant.COUNT) {
                header(HttpHeaders.Authorization, getInvalidBasicAuthHeader())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }
    }

    @Test
    fun `should allow access to users count endpoint WHEN credentials are valid`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get(RoutingConstant.COUNT) {
                header(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertTrue(response.bodyAsText().isNotBlank())

                val responseDeserialized = Json.decodeFromString<ServerResponse>(response.bodyAsText())
                assertEquals(validUserName, responseDeserialized.requestedBy)
                assertTrue(responseDeserialized.timestamp.isNotBlank())
                assertEquals(0, responseDeserialized.data.count)
            }
        }
    }

    @Test
    fun `should not allow access to users by ID endpoint WHEN credentials are missing`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get("${RoutingConstant.API_USERS}/123").apply {
                val response = call.response
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }
    }

    @Test
    fun `should not allow to access to users by ID endpoint WHEN credentials are bad`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get("${RoutingConstant.API_USERS}/123") {
                header(HttpHeaders.Authorization, getInvalidBasicAuthHeader())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }
    }

    @Test
    fun `should allow access to users by ID endpoint and return 404 WHEN credentials are valid and a user does not exist`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            client.get("${RoutingConstant.API_USERS}/123") {
                header(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.NotFound, response.status)
                assertTrue(response.bodyAsText().isNotBlank())

                val responseDeserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
                assertEquals(HttpStatusCode.NotFound.value, responseDeserialized.status)
                assertTrue(responseDeserialized.timestamp.isNotBlank())
                assertTrue(responseDeserialized.message.isNotBlank())
            }
        }
    }

    @Test
    fun `should allow access to users by ID endpoint WHEN credentials are valid and a user exists`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val id = runBlocking {
                userService.createUser(validNewUser).id
            }

            client.get("${RoutingConstant.API_USERS}/$id") {
                header(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                val response = call.response
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertTrue(response.bodyAsText().isNotBlank())
            }
        }
    }
}
