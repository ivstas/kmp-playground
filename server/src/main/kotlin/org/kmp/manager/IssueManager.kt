package org.kmp.manager

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val singleIssueSubscriptions = mutableSetOf<SingleItemSubscription<Issue>>()

    fun subscribeToIssue(scope: CoroutineScope, issueId: Int): InitializedFlow<Issue, IssueChangedEvent>? {
        val issue = getIssue(issueId)
            ?: return null

        val flow = MutableSharedFlow<IssueChangedEvent>()

        val subscription = object : SingleItemSubscription<Issue> {
            override fun check(item: Issue) = item.id == issueId
            override fun push(event: IssueChangedEvent) {
                scope.launch {
                    flow.emit(event)
                }
            }
        }

        singleIssueSubscriptions.add(subscription)
        // todo: unsubscribe when scope end
//        scope.coroutineContext.job.invokeOnCompletion {
//            singleIssueSubscriptions.remove(subscription)
//        }

        return InitializedFlow(issue, flow)
    }

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
        getIssueInTransaction(issueId)
    }

    private fun getIssueInTransaction(issueId: Int) = IssuesTable.selectAll().where {
        IssuesTable.id eq issueId
    }.map {
        Issue(
            id = it[IssuesTable.id].value,
            title = it[IssuesTable.title],
            assigneeId = it[IssuesTable.assigneeId],
            isCompleted = it[IssuesTable.isCompleted]
        )
    }.firstOrNull()

    fun setIsCompleted(issueId: Int, isCompleted: Boolean) {
        val issue = transaction(db) {
            // todo: update + select
            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.isCompleted] = isCompleted
            }

            getIssueInTransaction(issueId) ?: error("Issue not found")
        }

        singleIssueSubscriptions.forEach { subscription ->
            if (subscription.check(issue)) {
                subscription.push(IsCompletedChanged(isCompleted))
            }
        }
    }

    fun setTitle(issueId: Int, title: String) = transaction(db) {
        IssuesTable.update(where = { IssuesTable.id eq issueId }) {
            it[IssuesTable.title] = title
        }
    }
}

interface SingleItemSubscription<T> {
    fun check(item: T): Boolean
    fun push(event: IssueChangedEvent)
}
