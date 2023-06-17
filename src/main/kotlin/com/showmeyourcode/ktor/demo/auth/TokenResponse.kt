package com.showmeyourcode.ktor.demo.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String
)
