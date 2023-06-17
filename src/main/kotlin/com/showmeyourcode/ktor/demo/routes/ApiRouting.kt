package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.auth.JwtService
import com.showmeyourcode.ktor.demo.auth.TokenResponse
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.BASIC_AUTH_INTERNAL
import com.showmeyourcode.ktor.demo.response.writeDefaultBadRequestError
import com.showmeyourcode.ktor.demo.response.writeDefaultInternalServerError
import com.showmeyourcode.ktor.demo.user.RegisterUser
import com.showmeyourcode.ktor.demo.user.UserService
import com.showmeyourcode.ktor.demo.user.isEmailValid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val defaultBadMessage = "Cannot register a new user!"

fun Application.configureApiRouting(
    userService: UserService,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    log.info("Initializing API routing...")
    routing {
        post(RoutingConstant.API_USERS) {
            val signupObject = call.receive<RegisterUser>()
            this@configureApiRouting.log.info("Creating a new user with nick: {}", signupObject.nick)

            if (signupObject.email == null ||
                signupObject.password == null ||
                signupObject.nick == null
            ) {
                this@configureApiRouting.log.error("Bad request for registering a new user!")
                writeDefaultBadRequestError(call, defaultBadMessage)
                return@post
            }

            if (!isEmailValid(signupObject.email)) {
                this@configureApiRouting.log.error("Bad request for registering a new user with an invalid email address!")
                writeDefaultBadRequestError(call, defaultBadMessage)
                return@post
            }

            if (userService.getUserByEmail(signupObject.email) != null ||
                userService.getUserByNick(signupObject.nick) != null
            ) {
                this@configureApiRouting.log.error("A user already exists!")
                writeDefaultBadRequestError(call, "A user already exists!")
                return@post
            }

            val hash = hashFunction(signupObject.password)
            try {
                val newUser = userService.createUser(signupObject, hash)
                call.respond(
                    HttpStatusCode.Created,
                    TokenResponse(jwtService.generateAccessToken(newUser))
                )
            } catch (e: Throwable) {
                this@configureApiRouting.log.error("Failed to register user - {}", signupObject.nick, e)
                writeDefaultInternalServerError(call, defaultBadMessage)
            }
        }

        authenticate(BASIC_AUTH_INTERNAL) {
            get(RoutingConstant.API_USERS_STATS) {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("{ \"users\": { \"count\": 0 }, \"requestedBy\": \"$principal\" }")
            }
        }
    }
}
