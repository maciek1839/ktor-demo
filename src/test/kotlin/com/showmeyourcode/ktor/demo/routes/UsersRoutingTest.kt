package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.*
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.*
import com.showmeyourcode.ktor.demo.user.RegisterUser
import com.showmeyourcode.ktor.demo.user.UserService
import com.showmeyourcode.ktor.demo.user.toJson
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

val userService = UserService()

class UsersRoutingTest {

    @AfterTest
    fun afterTest() {
        runBlocking {
            userService.deleteAll()
        }
    }

    @Test
    fun `should create a new user WHEN data are valid`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(validNewUser.toJson())
            }.apply {
                assertEquals(HttpStatusCode.Created, response.status())
                assertNull(response.content)
                assertEquals(SERVICE_NAME_VALUE, response.headers[SERVICE_NAME_HEADER])
                assertNotNull(response.headers[HttpHeaders.Location])
            }
        }
    }

    @Test
    fun `should not create a user WHEN another user with the same email already exists`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            runBlocking {
                userService.createUser(validNewUser)
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
    fun `should not create a user WHEN email or password are empty`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser(null, "pswd").toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }

            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example@gmail.com", null).toJson())
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
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.API_USERS) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(RegisterUser("example.invalid-address", "pswd").toJson())
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not allow access to users count endpoint WHEN credentials are missing`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.COUNT) {
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }

            handleRequest(HttpMethod.Get, RoutingConstant.COUNT).apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should not allow to access to users count endpoint WHEN credentials are bad`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.COUNT) {
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }

            handleRequest(HttpMethod.Get, RoutingConstant.COUNT) {
                addHeader(HttpHeaders.Authorization, getInvalidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should allow access to users count endpoint WHEN credentials are valid`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, RoutingConstant.COUNT) {
                addHeader(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not allow access to users by ID endpoint WHEN credentials are missing`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, "${RoutingConstant.API_USERS}/123").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should not allow to access to users by ID endpoint WHEN credentials are bad`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, "${RoutingConstant.API_USERS}/123") {
                addHeader(HttpHeaders.Authorization, getInvalidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should allow access to users by ID endpoint and return 404 WHEN credentials are valid and a user does not exist`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            handleRequest(HttpMethod.Get, "${RoutingConstant.API_USERS}/123") {
                addHeader(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should allow access to users by ID endpoint WHEN credentials are valid and a user exists`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureSecurity()
            configureUsersRouting(userService)
        }) {
            val id = runBlocking {
                userService.createUser(validNewUser).id
            }
            handleRequest(HttpMethod.Get, "${RoutingConstant.API_USERS}/$id") {
                addHeader(HttpHeaders.Authorization, getValidBasicAuthHeader())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("application/json; charset=UTF-8", response.headers[HttpHeaders.ContentType])
                assertNotNull(response.content)
            }
        }
    }
}
