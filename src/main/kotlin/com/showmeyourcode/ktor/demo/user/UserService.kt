package com.showmeyourcode.ktor.demo.user

import com.showmeyourcode.ktor.demo.common.DATE_FORMAT
import com.showmeyourcode.ktor.demo.common.getLogger
import com.showmeyourcode.ktor.demo.database.UserEntity
import com.showmeyourcode.ktor.demo.database.UserId
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This function starts a coroutine for each query that runs on
 * a special thread pool “Dispatchers.IO” that is optimised for IO heavy operations.
 */
suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }

data class ValidationException(override val message: String, val status: HttpStatusCode) : Exception(message)

class UserService {

    companion object {
        private val logger = getLogger()
    }

    suspend fun deleteAll() = dbQuery {
        UserEntity.deleteAll()
    }

    suspend fun getAllCount(): UserCount = dbQuery {
        UserCount(
            UserEntity
                .slice(UserEntity.id, UserEntity.id.countDistinct().alias("count"))
                .selectAll().count()
        )
    }

    suspend fun getById(id: UserId): User? = dbQuery {
        UserEntity.select {
            (UserEntity.id eq id)
        }.mapNotNull { e -> e.toUser() }
            .singleOrNull()
    }

    suspend fun createUser(signupObject: RegisterUser): User {
        if (signupObject.email == null || signupObject.password == null) {
            throw ValidationException("Bad request for registering a new user", HttpStatusCode.BadRequest)
        }

        if (!isEmailValid(signupObject.email)) {
            throw ValidationException(
                "Bad request for registering a new user with an invalid email address",
                HttpStatusCode.BadRequest
            )
        }

        if (getUserByEmail(signupObject.email) != null) {
            throw ValidationException("A user already exists", HttpStatusCode.BadRequest)
        }

        return dbQuery {
            return@dbQuery UserEntity.insert {
                it[email] = signupObject.email
                it[password] = hashFunction(signupObject.password)
                it[active] = true
            }.toUser()
        }
    }

    private suspend fun getUserByEmail(email: String): User? = dbQuery {
        UserEntity.select {
            (UserEntity.email eq email)
        }.mapNotNull { e -> e.toUser() }
            .singleOrNull()
    }
}

fun InsertStatement<Number>.toUser(): User =
    User(
        id = this[UserEntity.id],
        email = this[UserEntity.email],
        active = this[UserEntity.active],
        password = this[UserEntity.password],
        createAt = this[UserEntity.createdAt].toString(DATE_FORMAT)
    )

fun ResultRow.toUser(): User =
    User(
        id = this[UserEntity.id],
        email = this[UserEntity.email],
        active = this[UserEntity.active],
        password = this[UserEntity.password],
        createAt = this[UserEntity.createdAt].toString(DATE_FORMAT)
    )
