package com.showmeyourcode.ktor.demo.user

import com.showmeyourcode.ktor.demo.database.UserEntity
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

class UserService {

    suspend fun deleteAll() = dbQuery {
        UserEntity.deleteAll()
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        UserEntity.select {
            (UserEntity.email eq email)
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    suspend fun getUserByNick(nick: String): User? = dbQuery {
        UserEntity.select {
            (UserEntity.nick eq nick)
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    suspend fun createUser(newUser: RegisterUser, hash: String): User = dbQuery {
        val insert = UserEntity.insert {
            it[email] = newUser.email!!
            it[password] = hash
            it[nick] = newUser.nick!!
            it[active] = true
        }
        toUser(insert)
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[UserEntity.id],
            email = row[UserEntity.email],
            active = row[UserEntity.active],
            password = row[UserEntity.password],
            nick = row[UserEntity.nick],
            createAt = row[UserEntity.createdAt].toString("yyyy-MM-dd'T'HH:mm:ss.SSZZ")
        )

    private fun toUser(row: InsertStatement<Number>): User =
        User(
            id = row[UserEntity.id],
            email = row[UserEntity.email],
            active = row[UserEntity.active],
            password = row[UserEntity.password],
            nick = row[UserEntity.nick],
            createAt = row[UserEntity.createdAt].toString("yyyy-MM-dd'T'HH:mm:ss.SSZZ")
        )
}
