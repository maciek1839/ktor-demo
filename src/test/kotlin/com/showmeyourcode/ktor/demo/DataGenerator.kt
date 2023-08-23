package com.showmeyourcode.ktor.demo

import com.showmeyourcode.ktor.demo.user.RegisterUser
import java.util.*

val validNewUser = RegisterUser("example@mail.com", "pswd")

val validUserName = "user"

fun getValidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("$validUserName:$validUserName").toByteArray())

fun getInvalidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("test" + ":" + "invalid").toByteArray())
