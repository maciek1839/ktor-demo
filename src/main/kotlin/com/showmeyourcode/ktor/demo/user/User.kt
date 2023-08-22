package com.showmeyourcode.ktor.demo.user

import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class User(
    val id: Int,
    val email: String,
    val active: Boolean,
    val password: String,
    val createAt: String
)

@Serializable
data class RegisterUser(
    val email: String?,
    val password: String?
)

@Serializable
data class UserCount(
    val count: Int
)

fun User.toJson(): String = DefaultJson.encodeToString(this)
fun RegisterUser.toJson(): String = DefaultJson.encodeToString(this)

fun isEmailValid(email: String): Boolean {
    return "^[A-Za-z](.*)([@])(.+)(\\.)(.+)".toRegex().matches(email)
}
