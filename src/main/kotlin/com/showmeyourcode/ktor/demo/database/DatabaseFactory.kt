package com.showmeyourcode.ktor.demo.database

import com.showmeyourcode.ktor.demo.configuration.appConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    private val dbUrl = appConfig.property("db.jdbcUrl").getString()
    private val dbUser = appConfig.property("db.dbUser").getString()
    private val schema = appConfig.property("db.schema").getString()
    private val dbPassword = appConfig.property("db.dbPassword").getString()
    private val driver = appConfig.property("db.driver").getString()

    fun init() {
        val dataSource = hikari()
        Database.connect(dataSource)
        Flyway.configure()
            .locations("classpath:db/migration")
            .defaultSchema(schema)
            .schemas(schema)
            .createSchemas(true)
            .dataSource(dataSource)
            .load()
            .migrate()
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = driver
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}
