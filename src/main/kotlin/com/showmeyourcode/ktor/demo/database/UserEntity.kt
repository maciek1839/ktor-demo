package com.showmeyourcode.ktor.demo.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object UserEntity : Table("user_mgmt.sample_user") {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val email: Column<String> = varchar("email", 100)
    val nick: Column<String> = varchar("nick", 100)
    val password: Column<String> = varchar("password", 100)
    val active: Column<Boolean> = bool("active")
    val createdAt: Column<DateTime> = datetime("created_at").clientDefault { DateTime.now() }
}
