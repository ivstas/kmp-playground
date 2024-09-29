package org.kmp.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.kmp.*
import org.kmp.IssuesTable
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

    fun listenToIssues(scope: CoroutineScope): IssueListUpdates {
        val issues = getIssues()

        val listChangedFlow = MutableSharedFlow<IterableModificationEvent<Int, Issue>>()

        val subscription = IssueListUpdates(
            listChangedFlow = listChangedFlow,
            elementChangedFlow = MutableSharedFlow()
        )


//        allIssueSubscriptions.add(subscription)

//        scope.toLifetime().callWhenTerminated {
//            allIssueSubscriptions.remove(subscription)
//        }

        // fixme: create a scope from lifetime
        scope.launch {
            delay(500)
            listChangedFlow.emit(IterableModificationEventReset(issues))

            delay(500)
            listChangedFlow.emit(IterableModificationEventRemoved(3))
        }

        return subscription
    }

    fun getIssue(issueId: Int) = transaction(db) {
        IssuesTable.selectAll().where {
            IssuesTable.id eq issueId
        }.map {
            Issue(
                id = it[IssuesTable.id].value,
                title = it[IssuesTable.title],
                assigneeId = it[IssuesTable.assigneeId],
                isCompleted = it[IssuesTable.isCompleted]
            )
        }.firstOrNull()
    }

    fun setIsCompleted(issueId: Int, isCompleted: Boolean) = transaction(db) {
        IssuesTable.update(where = { IssuesTable.id eq issueId }) {
            it[IssuesTable.isCompleted] = isCompleted
        }
    }

    fun setTitle(issueId: Int, title: String) = transaction(db) {
        IssuesTable.update(where = { IssuesTable.id eq issueId }) {
            it[IssuesTable.title] = title
        }
    }
}
