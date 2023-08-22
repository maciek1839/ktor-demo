package com.showmeyourcode.ktor.demo.routes

import com.showmeyourcode.ktor.demo.constant.RoutingConstant
import com.showmeyourcode.ktor.demo.plugins.BASIC_AUTH_INTERNAL
import com.showmeyourcode.ktor.demo.response.*
import com.showmeyourcode.ktor.demo.user.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val defaultBadMessage = "Cannot register a new user!"

fun Application.configureUsersRouting(
    userService: UserService
) {
    log.info("Initializing Users routing...")
    routing {
        post(RoutingConstant.API_USERS) {
            val signupObject = call.receive<RegisterUser>()
            try {
                this@configureUsersRouting.log.info("Creating a new user: {}", signupObject.email)
                val newUser = userService.createUser(signupObject)
                call.response.headers.append(HttpHeaders.Location, "${RoutingConstant.API_USERS}/${newUser.id}")
                call.respond(
                    HttpStatusCode.Created
                )
            } catch (e: ValidationException) {
                this@configureUsersRouting.log.error("Validation exception for creating a user - {}", signupObject.email, e)
                writeRequestError(call, e.message, e.status)
            } catch (e: Throwable) {
                this@configureUsersRouting.log.error("Failed to register a user - {}", signupObject.email, e)
                writeDefaultInternalServerError(call, defaultBadMessage)
            }
        }

        authenticate(BASIC_AUTH_INTERNAL) {
            get("${RoutingConstant.API_USERS}/{userId}") {
                val user = userService.getById(call.parameters["userId"]?.toInt()!!)
                if (user == null) {
                    writeDefaultNotFoundRequestError(call, "User is not found")
                } else {
                    call.respondText(user.toJson(), ContentType.Application.Json)
                }
            }
        }

        authenticate(BASIC_AUTH_INTERNAL) {
            get(RoutingConstant.COUNT) {
                val principal = call.principal<UserIdPrincipal>()
                call.respondText(
                    ServerResponse(userService.getAllCount(), principal!!.name).toJson(),
                    ContentType.Application.Json
                )
            }
        }
    }
}
