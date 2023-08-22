package com.showmeyourcode.ktor.demo.oauth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.showmeyourcode.ktor.demo.common.getLogger
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.server.config.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Serializable
data class OAuthTokenResponse(
    val accessToken: String,
    val expiresAt: String,
    val refreshToken: String,
    val refreshedTokenExpiresAt: String,
    val tokenType: String = "Bearer"
)

@Serializable
data class OAuthTokenStatus(
    val scopes: List<String>,
    val expiresAt: String,
    val clientId: String
)

fun OAuthTokenStatus.toJson() = DefaultJson.encodeToString(this)

fun DecodedJWT.toTokenStatus() = OAuthTokenStatus(
    claims[OAuthService.CLAIM_SCOPE]?.asString()?.split(" ") ?: emptyList(),
    expiresAt.toString(),
    claims[OAuthService.CLAIM_ID]?.asString() ?: "???"
)

fun OAuthTokenResponse.toJson() = DefaultJson.encodeToString(this)

class OAuthService {

    companion object {
        private val logger = getLogger()

        // Issuer (iss)
        // Identifies the security token service (STS) that constructs and returns the token. It also identifies the directory in which the user was authenticated. Your application should validate the issuer claim to make sure that the token came from the appropriate endpoint.
        // Ref: https://learn.microsoft.com/en-us/azure/active-directory-b2c/tokens-overview
        const val CLAIM_ISSUER_VALUE = "https://showmeyourcode.server.example.com"

        // Audience (aud)
        // Identifies the intended recipient of the token. The audience is the application ID.
        // Your application should validate this value and reject the token if it doesn't match.
        // Audience is synonymous with resource.
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

    fun authorize(state: String, clientId: String, redirectUri: String, scope: String): String {
        // For test purposes all params are logged.
        logger.info("Processing 'authorize' request: state - $state, clientId - $clientId, redirectUri - $redirectUri, scope - $scope")
        return "$redirectUri?code=CODE_FROM_AUTHORIZE&state=$state"
    }

    fun generateToken(clientId: String, clientSecret: String, code: String, grantType: String): OAuthTokenResponse {
        // For test purposes all params are logged.
        logger.info("Processing 'token' request: clientId - $clientId, clientSecret - $clientSecret, code - $code, grantType - $grantType")

        val accessTokenExpiresAt = Instant.now().toEpochMilli()
        val refreshedTokenExpiresAt = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()
        return OAuthTokenResponse(
            JWT.create()
                .withSubject(CLAIM_SUBJECT_VALUE)
                .withIssuer(CLAIM_ISSUER_VALUE)
                .withAudience(CLAIM_AUDIENCE_VALUE)
                .withIssuedAt(Date())
                .withClaim(CLAIM_ID, clientId)
                .withClaim(CLAIM_SCOPE, "openid profile email")
                .withExpiresAt(expiresAt())
                .sign(algorithm),
            accessTokenExpiresAt.toString(),
            JWT.create()
                .withSubject(CLAIM_SUBJECT_VALUE)
                .withIssuer(CLAIM_ISSUER_VALUE)
                .withAudience(CLAIM_AUDIENCE_VALUE)
                .withIssuedAt(Date(Instant.now().toEpochMilli()))
                .withClaim(CLAIM_ID, clientId)
                .withClaim(CLAIM_SCOPE, "refresh")
                .withExpiresAt(expiresAt())
                .sign(algorithm),
            refreshedTokenExpiresAt.toString()
        )
    }

    fun getTokenStatus(accessTokenHeader: String): OAuthTokenStatus {
        val accessToken = accessTokenHeader.substring(7)
        return verifyToken(accessToken).toTokenStatus()
    }

    fun revokeToken(accessClientHeader: String, accessToken: String, clientId: String) {
        // For test purposes all params are logged.
        logger.info("Processing 'revoke' request: accessClientHeader - $accessClientHeader, accessToken - $accessToken, clientId - $clientId")
    }

    private fun verifyToken(token: String): DecodedJWT = verifier.verify(token)

    private fun expiresAt() = Date(System.currentTimeMillis() + 3_600_000)
}
