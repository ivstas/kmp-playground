package org.kmp.manager

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.db.tables.IssuesTable
import org.kmp.domain.IssueIn

class IssueManager(private val db: Database) {
    fun addIssue(issueIn: IssueIn) = transaction(db) {
        IssuesTable.insert {
            it[title] = issueIn.title
        } get IssuesTable.id
    }.value
}