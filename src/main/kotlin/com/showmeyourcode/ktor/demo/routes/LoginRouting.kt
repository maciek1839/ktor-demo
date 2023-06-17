package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.auth.JwtService
import com.showmeyourcode.ktor.demo.auth.TokenResponse
import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.response.writeDefaultBadRequestError
import com.showmeyourcode.ktor.demo.response.writeDefaultNotFoundRequestError
import com.showmeyourcode.ktor.demo.user.LoginUser
import com.showmeyourcode.ktor.demo.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting(
    userService: UserService,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    log.info("Initializing login routing...")
    routing {
        post(RoutingConstant.LOGIN) {
            val loginUser = call.receive<LoginUser>()
            val dbUser = userService.getUserByEmail(loginUser.email)

            if (dbUser == null) {
                this@configureLoginRouting.log.error("Such a user does not exist - {}", loginUser.email)
                writeDefaultNotFoundRequestError(call, "Such a user does not exist - ${loginUser.email}")
                return@post
            }

            if (dbUser.password != hashFunction(loginUser.password)) {
                this@configureLoginRouting.log.error("Wrong password! {}", loginUser.email)
                writeDefaultBadRequestError(call, "Wrong password!")
                return@post
            }

            if (!dbUser.active) {
                this@configureLoginRouting.log.error("The user is not active! {}", loginUser.email)
                writeDefaultBadRequestError(call, "The user is not active!")
                return@post
            }

            call.respond(
                HttpStatusCode.OK,
                TokenResponse(jwtService.generateAccessToken(dbUser))
            )
        }
    }
}
