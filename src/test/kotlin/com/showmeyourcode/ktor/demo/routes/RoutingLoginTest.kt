package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.auth.hashFunction
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.invalidPasswordUserLogin
import com.showmeyourcode.ktor.demo.plugins.configureFlyway
import com.showmeyourcode.ktor.demo.plugins.configureSerialization
import com.showmeyourcode.ktor.demo.user.toJson
import com.showmeyourcode.ktor.demo.validNewUser
import com.showmeyourcode.ktor.demo.validUserLogin
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RoutingLoginTest {

    @AfterTest
    fun afterTest() {
        runBlocking {
            userService.deleteAll()
        }
    }

    @Test
    fun `should log in with an existing user`() {
        withTestApplication({
            configureFlyway()
            configureSerialization()
            configureLoginRouting(userService, jwtService, hashFunction)
        }) {
            runBlocking {
                userService.createUser(validNewUser, hashFunction(validNewUser.password!!))
            }.apply {
                handleRequest(HttpMethod.Post, RoutingConstant.LOGIN) {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(validUserLogin.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)
                }
            }
        }
    }

    @Test
    fun `should not log in WHEN a password is incorrect`() {
        withTestApplication({
            configureFlyway()
            configureSerialization()
            configureLoginRouting(userService, jwtService, hashFunction)
        }) {
            runBlocking {
                userService.createUser(validNewUser, hashFunction(validNewUser.password!!))
            }.apply {
                handleRequest(HttpMethod.Post, RoutingConstant.LOGIN) {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(invalidPasswordUserLogin.toJson())
                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)
                }
            }
        }
    }

    @Test
    fun `should not log in WHEN a user does not exist`() {
        withTestApplication({
            configureSerialization()
            configureFlyway()
            configureLoginRouting(userService, jwtService, hashFunction)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.LOGIN) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(validUserLogin.toJson())
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertNotNull(response.content)
            }
        }
    }
}
