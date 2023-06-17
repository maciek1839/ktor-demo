package com.showmeyourcode.ktor.demo

import com.showmeyourcode.ktor.demo.user.LoginUser
import com.showmeyourcode.ktor.demo.user.RegisterUser
import com.showmeyourcode.ktor.demo.user.User
import java.time.Instant
import java.util.*

val validNewUser = RegisterUser("example@mail.com", "pswd", "exampleNick")
val validNewUserTheSameNick = RegisterUser("example2@mail.com", "pswd", "exampleNick")
val validUser = User(1, "example@mail.com", "pswd", true, "pswd", Instant.now().toString())
val validUserLogin = LoginUser("example@mail.com", "pswd")
val invalidPasswordUserLogin = LoginUser("example@mail.com", "pswd")

fun getValidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("user" + ":" + "user").toByteArray())

fun getInvalidBasicAuthHeader() = "Basic " + Base64.getEncoder().encodeToString(("test" + ":" + "invalid").toByteArray())
