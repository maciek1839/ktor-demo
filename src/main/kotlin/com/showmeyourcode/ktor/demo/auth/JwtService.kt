package com.showmeyourcode.ktor.demo.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.showmeyourcode.ktor.demo.user.User
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import io.ktor.util.*
import java.time.Instant
import java.util.*

class JwtService {

    companion object {
        val CLAIM_ISSUER_VALUE = "showmeyourcode"
        val CLAIM_SUBJECT_VALUE = "Authentication"
        val CLAIM_ID = "id"
        val CLAIM_NICKNAME = "nickname"
        val CLAIM_SCOPE = "scope"
    }

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val jwtSecret = hex(appConfig.property("auth.jwtSecret").getString())
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(CLAIM_ISSUER_VALUE)
        .build()

    fun verifyToken(token: String): DecodedJWT = verifier.verify(token)

    fun generateAccessToken(user: User): String = JWT.create()
        .withSubject(CLAIM_SUBJECT_VALUE)
        .withIssuer(CLAIM_ISSUER_VALUE)
        .withIssuedAt(Date(Instant.now().toEpochMilli()))
        .withClaim(CLAIM_ID, user.id)
        .withClaim(CLAIM_NICKNAME, user.nick)
        .withClaim(CLAIM_SCOPE, "openid profile email")
        .withExpiresAt(expiresAt())
        .sign(algorithm)

    private fun expiresAt() = Date(System.currentTimeMillis() + 3_600_000 * 24) // 24 hours
}
