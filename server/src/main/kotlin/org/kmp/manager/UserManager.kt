package org.kmp.manager

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.User
import org.kmp.UsersTable

class UserManager(private val db: Database) {
    fun addUser(userName: String) = transaction(db) {
        UsersTable.insert {
            it[name] = userName
        } get UsersTable.id
    }.value

    fun getUser(userId: Int): User? = transaction(db) {
        UsersTable.selectAll().where {
            UsersTable.id eq userId
        }.map {
            User(
                id = it[UsersTable.id].value,
                name = it[UsersTable.name],
            )
        }.firstOrNull()
    }
}