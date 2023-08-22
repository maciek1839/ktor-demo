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
import java.time.temporal.ChronoUnit
import java.util.*

// {
//  "access_token":"eyJz93a...k4laUWw",
//  "token_type":"Bearer",
//  "expires_in":86400
// }

data class UserToken(
    val accessToken: String,
    val expiresAt: String,
    val refreshToken: String,
    val refreshedTokenExpiresAt: String,
    val tokenType: String = "Bearer"
)

/**
 * Generate an access token: https://auth0.com/docs/secure/tokens/access-tokens/get-access-tokens
 * Generate a refresh token:
 * - https://auth0.com/docs/secure/tokens/refresh-tokens/get-refresh-tokens
 * - https://developer.squareup.com/docs/oauth-api/refresh-revoke-limit-scope
 */
class JwtService {

    companion object {
        // Issuer (iss)
        // https://<tenant-name>.b2clogin.com/775527ff-9a37-4307-8b3d-cc311f58d925/v2.0/
        // Identifies the security token service (STS) that constructs and returns the token. It also identifies the directory in which the user was authenticated. Your application should validate the issuer claim to make sure that the token came from the appropriate endpoint.
        // Ref: https://learn.microsoft.com/en-us/azure/active-directory-b2c/tokens-overview
        const val CLAIM_ISSUER_VALUE = "https://showmeyourcode.server.example.com"

        // Audience (aud)
        // 90c0fe63-bcf2-44d5-8fb7-b8bbc0b29dc6
        // Identifies the intended recipient of the token. For Azure AD B2C, the audience is the application ID. Your application should validate this value and reject the token if it doesn't match. Audience is synonymous with resource.
        // Ref: https://learn.microsoft.com/en-us/azure/active-directory-b2c/tokens-overview
        const val CLAIM_AUDIENCE_VALUE = "ktor-application-id"
        const val CLAIM_SUBJECT_VALUE = "Authentication"
        const val CLAIM_ID = "id"
        const val CLAIM_SCOPE = "scope"
    }

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val jwtSecret = hex(appConfig.property("auth.jwtSecret").getString())
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(CLAIM_ISSUER_VALUE)
        .build()

    fun verifyToken(token: String): DecodedJWT = verifier.verify(token)

    fun generateToken(user: User): UserToken {
        val accessTokenExpiresAt = Instant.now().toEpochMilli()
        // Refresh tokens are typically longer-lived
        // and can be used to request new access tokens after the shorter-lived access tokens expire.
        // Ref: https://auth0.com/docs/secure/tokens/refresh-tokens/refresh-token-rotation
        val refreshedTokenExpiresAt = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()
        return UserToken(
            JWT.create()
                .withSubject(CLAIM_SUBJECT_VALUE)
                .withIssuer(CLAIM_ISSUER_VALUE)
                .withAudience(CLAIM_AUDIENCE_VALUE)
                .withIssuedAt(Date())
                .withClaim(CLAIM_ID, user.id)
                .withClaim(CLAIM_SCOPE, "openid profile email")
                .withExpiresAt(expiresAt())
                .sign(algorithm),
            accessTokenExpiresAt.toString(),
            JWT.create()
                .withSubject(CLAIM_SUBJECT_VALUE)
                .withIssuer(CLAIM_ISSUER_VALUE)
                .withIssuedAt(Date(Instant.now().toEpochMilli()))
                .withClaim(CLAIM_ID, user.id)
                .withClaim(CLAIM_SCOPE, "openid profile email")
                .withExpiresAt(expiresAt())
                .sign(algorithm),
            refreshedTokenExpiresAt.toString()
        )
    }

    private fun expiresAt() = Date(System.currentTimeMillis() + 3_600_000)
}
