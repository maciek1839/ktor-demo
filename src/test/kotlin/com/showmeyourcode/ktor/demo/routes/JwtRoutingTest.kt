package com.showmeyourcode.ktor.demo.routes

import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest

class JwtRoutingTest {

    @AfterTest
    fun afterTest() {
        runBlocking {
            userService.deleteAll()
        }
    }
}
