package com.showmeyourcode.ktor.demo

import com.showmeyourcode.ktor.demo.user.RegisterUser
import java.util.*

val validNewUser = RegisterUser("example@mail.com", "pswd")

fun getValidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("user" + ":" + "user").toByteArray())

fun getInvalidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("test" + ":" + "invalid").toByteArray())
