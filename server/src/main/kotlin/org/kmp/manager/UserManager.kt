package org.kmp.manager

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.UsersTable

class UserManager(private val db: Database) {
    fun addUser(userName: String) = transaction(db) {
        UsersTable.insert {
            it[name] = userName
        } get UsersTable.id
    }.value
}