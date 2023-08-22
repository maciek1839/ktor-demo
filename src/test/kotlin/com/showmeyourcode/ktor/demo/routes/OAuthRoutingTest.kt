package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.oauth.OAuthService
import com.showmeyourcode.ktor.demo.oauth.OAuthTokenResponse
import com.showmeyourcode.ktor.demo.oauth.OAuthTokenStatus
import com.showmeyourcode.ktor.demo.plugins.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

val oauthService = OAuthService()

class OAuthRoutingTest {
    @Test
    fun `should generate an authorization code and redirect WHEN params are valid for the authorize endpoint`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            val clientId = "YOUR_APP_ID"
            val scope = "PROFILE+API_READ"
            val redirectUrl = "https://your-app.localhost/callback"
            val state = "82201dd8d83d23cc8a48caf52b"
            val authorizeUrl = "${RoutingConstant.OAUTH_AUTHORIZE}?client_id=$clientId&scope=$scope&redirect_uri=$redirectUrl&state=$state"
            handleRequest(HttpMethod.Get, authorizeUrl) {
            }.apply {
                assertEquals(HttpStatusCode.Found, response.status())
                assertEquals("https://your-app.localhost/callback?code=CODE_FROM_AUTHORIZE&state=82201dd8d83d23cc8a48caf52b", response.headers[HttpHeaders.Location])
            }
        }
    }

    @Test
    fun `should return 400 WHEN any param is missing for the authorize endpoint`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            val scope = "profile+email"
            val redirectUrl = "https://your-app.localhost/callback"
            val state = "82201dd8d83d23cc8a48caf52b"
            val authorizeUrl = "${RoutingConstant.OAUTH_AUTHORIZE}?scope=$scope&redirect_uri=$redirectUrl&state=$state"
            handleRequest(HttpMethod.Get, authorizeUrl) {
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `should generate access and refresh tokens WHEN params are valid for the token endpoint`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            val clientId = "YOUR_APP_ID"
            val clientSecret = "APPLICATION_SECRET"
            val code = "CODE_FROM_AUTHORIZE"
            val grantType = "authorization_code"
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH) {
                addHeader(HttpHeaders.ContentType, "application/json")
                this.setBody(
                    """
                    {
                      "client_id": "$clientId",
                      "client_secret": "$clientSecret",
                      "code": "$code",
                      "grant_type": "$grantType"
                    }
                    """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                // ensure deserialization is successful
                val response = Json.decodeFromString<OAuthTokenResponse>(response.content!!)
            }
        }
    }

    @Test
    fun `should not generate tokens WHEN params are missing for the token endpoint`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            val clientId = "YOUR_APP_ID"
            val clientSecret = "APPLICATION_SECRET"
            val code = "CODE_FROM_AUTHORIZE"
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH) {
                addHeader(HttpHeaders.ContentType, "application/json")
                this.setBody(
                    """
                    {
                      "client_id": "$clientId",
                      "client_secret": "$clientSecret",
                      "code": "$code"
                    }
                    """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
            }
        }
    }

    @Test
    fun `should get a token details WHEN the token is valid`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            val validAccessToken = oauthService.generateToken(
                "YOUR_APP_ID",
                "APPLICATION_SECRET",
                "CODE_FROM_AUTHORIZE",
                "authorization_code"
            )
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_STATUS) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Authorization, "Bearer ${validAccessToken.accessToken}")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)

                val response = Json.decodeFromString<OAuthTokenStatus>(response.content!!)
                assertEquals("YOUR_APP_ID", response.clientId)
            }
        }
    }

    @Test
    fun `should not get a token details and return 401 WHEN the token is expired`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_STATUS) {
                val expiredAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImF1ZCI6Imt0b3ItYXBwbGljYXRpb24taWQiLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJpc3MiOiJodHRwczovL3Nob3dtZXlvdXJjb2RlLnNlcnZlci5leGFtcGxlLmNvbSIsImlkIjoiIiwiZXhwIjoxNjkyNzE2MzEyLCJpYXQiOjE2OTI3MTI3MTJ9.9eOsdW5cuB-sCeAc6kyZMUY5BJgsvXl9sJDECiwvhw_TTjnKmIVuY01XbsJWOrphs2Yl49BvsUrSIYp67KgkWA"
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Authorization, "Bearer $expiredAccessToken")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should not get a token details and return 401 WHEN the token is missing`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_STATUS) {
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }
    }

    @Test
    fun `should revoke a refresh token WHEN params are valid`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_REVOKE) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Authorization, "Client APPLICATION_SECRET")
                this.setBody(
                    """
                    {
                    	"access_token": "ACCESS_TOKEN",
                    	"client_id": "CLIENT_ID"
                    }
                    """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not revoke a refresh token WHEN the authorization header is missing`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_REVOKE) {
                addHeader(HttpHeaders.ContentType, "application/json")
                this.setBody(
                    """
                    {
                    	"access_token": "ACCESS_TOKEN",
                    	"client_id": "CLIENT_ID"
                    }
                    """.trimIndent()
                )
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }
        }
    }

    @Test
    fun `should not revoke a refresh token WHEN client ID or access token are missing`() {
        withTestApplication({
            configureSerialization()
            configureHttp()
            configureSecurity()
            configureOAuthRouting(oauthService)
        }) {
            handleRequest(HttpMethod.Post, RoutingConstant.OAUTH_REVOKE) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Authorization, "Client APPLICATION_SECRET")
                this.setBody("{ }")
            }.apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertNotNull(response.content)
            }
        }
    }
}
