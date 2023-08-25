package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.oauth.OAuthService
import com.showmeyourcode.ktor.demo.oauth.toJson
import com.showmeyourcode.ktor.demo.response.writeDefaultBadRequestError
import com.showmeyourcode.ktor.demo.response.writeRequestError
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureOAuthRouting(oauthService: OAuthService) {
    log.info("Initializing OAuth routing...")
    routing {
        get(RoutingConstant.OAUTH_AUTHORIZE) {
            this@configureOAuthRouting.log.info("Received an authorize request with params: {}", call.parameters)

            val state = call.parameters["state"]
            val clientId = call.parameters["client_id"]
            val redirectUri = call.parameters["redirect_uri"]
            val scope = call.parameters["scope"]

            if (state == null || clientId == null || redirectUri == null || scope == null) {
                writeDefaultBadRequestError(call, "Missing parameters")
                return@get
            }

            val redirectTo = oauthService.authorize(state, clientId, redirectUri, scope)
            this@configureOAuthRouting.log.info("Redirecting to $redirectTo")
            call.respondRedirect(redirectTo)
        }

        post(RoutingConstant.OAUTH) {
            val body = call.receive<Map<String, String>>()
            val code = body["code"]
            val clientId = body["client_id"]
            val clientSecret = body["client_secret"]
            val grantType = body["grant_type"]

            if (code == null || clientId == null || clientSecret == null || grantType == null) {
                writeDefaultBadRequestError(call, "Missing parameters")
                return@post
            }

            val tokenResponse = oauthService.generateToken(clientId, clientSecret, code, grantType)
            call.respondText(tokenResponse.toJson(), ContentType.Application.Json)
        }

        post(RoutingConstant.OAUTH_STATUS) {
            suspend fun returnUnauthorized(call: ApplicationCall) {
                writeRequestError(call, "Missing the authorization header", HttpStatusCode.Unauthorized)
            }

            val accessTokenHeader = call.request.headers[HttpHeaders.Authorization]

            if (accessTokenHeader == null) {
                returnUnauthorized(call)
                return@post
            }

            try {
                val status = oauthService.getTokenStatus(accessTokenHeader)
                call.respondText(status.toJson(), ContentType.Application.Json)
            } catch (e: Exception) {
                // For test purposes the token is logged.
                this@configureOAuthRouting.log.error("Cannot process the token '$accessTokenHeader'", e)
                returnUnauthorized(call)
                return@post
            }
        }

        post(RoutingConstant.OAUTH_REVOKE) {
            val accessClientHeader = call.request.headers[HttpHeaders.Authorization]
            val body = call.receive<Map<String, String>>()
            val accessToken = body["access_token"]
            val clientId = body["client_id"]

            if (clientId == null || accessToken == null || accessClientHeader == null) {
                writeDefaultBadRequestError(call, "Missing parameters")
                return@post
            }

            oauthService.revokeToken(accessClientHeader, accessToken, clientId)
            call.respondText(
                """
                {
                    "success": true
                }
                """.trimIndent(),
                ContentType.Application.Json
            )
            return@post
        }
    }
}
