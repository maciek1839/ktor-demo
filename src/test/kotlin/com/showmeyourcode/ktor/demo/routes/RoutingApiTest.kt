package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.*
import com.showmeyourcode.ktor.demo.auth.JwtService
import com.showmeyourcode.ktor.demo.auth.hashFunction
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.*
import com.showmeyourcode.ktor.demo.user.RegisterUser
import com.showmeyourcode.ktor.demo.user.UserService
import com.showmeyourcode.ktor.demo.user.toJson
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

val jwtService = JwtService()
val userService = UserService()

class RoutingApiTest {

    @AfterTest
    fun afterTest() {
        runBlocking {
            userService.deleteAll()
        }
    }

    @Test
    fun `should create a new user`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(validNewUser.toJson())
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertNotNull(response.content)
                assertEquals(SERVICE_NAME_VALUE, response.headers[SERVICE_NAME_HEADER])
            }
        }
    }

    @Test
    fun `should not create a user with the same email`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            runBlocking {
                userService.createUser(validNewUser, hashFunction(validNewUser.password!!))
            }.apply {
                handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(validNewUser.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.BadRequest, response.status())
                    assertNotNull(response.content)
                }
            }
        }
    }

    @Test
    fun `should not create user with the same nick`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            runBlocking {
                userService.createUser(validNewUser, hashFunction(validNewUser.password!!))
            }.apply {
                handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(validNewUserTheSameNick.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.BadRequest, response.status())
                    assertNotNull(response.content)
                }
            }
        }
    }

    @Test
    fun `should not create a user WHEN email, nick or password are empty`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser(null, "pswd", "nick").toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }

            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example@gmail.com", null, "exampleNick").toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }

            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example@mail.com", "pswd", null).toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not create a user WHEN an email is invalid`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example.invalid-address", "pswd", "exampleNick").toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not allow access without or bad credentials`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.API_USERS_STATS) {
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }

            handleRequest(HttpMethod.Get, RoutingConstant.API_USERS_STATS) {
                addHeader("Authorization", getInvalidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should allow access to a protected resource 'api_v1_users_stats'`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureApiRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.API_USERS_STATS) {
                addHeader("Authorization", getValidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }
    }
}
