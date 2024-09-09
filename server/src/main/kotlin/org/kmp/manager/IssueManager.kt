package org.kmp.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.*
import org.kmp.db.tables.IssuesTable
import org.rsp.*


class IssueManager(private val db: Database) {
    fun addIssue(issueIn: IssueIn) = transaction(db) {
        IssuesTable.insert {
            it[title] = issueIn.title
        } get IssuesTable.id
    }.value

    fun getIssues() = transaction(db) {
        IssuesTable.selectAll().map {
            Issue(
                id = it[IssuesTable.id].value,
                title = it[IssuesTable.title],
                assigneeId = it[IssuesTable.assigneeId],
                isCompleted = it[IssuesTable.isCompleted]
            )
        }
    }

    private val allIssueSubscriptions = mutableSetOf<Flow<IssuesModificationEvent>>()

    fun listenToIssues(scope: CoroutineScope): Flow<IssuesModificationEvent> {
        val subscription = MutableSharedFlow<IssuesModificationEvent>()

        allIssueSubscriptions.add(subscription)

//        scope.toLifetime().callWhenTerminated {
//            allIssueSubscriptions.remove(subscription)
//        }

        // fixme: create a scope from lifetime
        scope.launch {
            val issues = getIssues()
            subscription.emit(IssuesModificationEvent.ListModification(IterableModificationEventReset(issues)))
        }

        return subscription
    }
}
