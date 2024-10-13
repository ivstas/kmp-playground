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

    private val issueChangeSubscriptions = mutableSetOf<IssueChangedCheckedSubscription>()
    private fun broadcastIssueModificationEvent(entity: Issue, event: IssueChangedEvent) {
        issueChangeSubscriptions.forEach { subscription ->
            if (subscription.check(entity)) {
                subscription.emit(event)
            }
        }
    }

    fun subscribeToIssue(scope: CoroutineScope, issueId: Int): InitializedFlow<Issue, IssueChangedEvent>? {
        val issue = getIssue(issueId)
            ?: return null

        val flow = MutableSharedFlow<IssueChangedEvent>()

        val subscription: IssueChangedCheckedSubscription = FlowSubscription(scope, flow) { it.id == issueId }

        issueChangeSubscriptions.add(subscription)
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

        broadcastIssueModificationEvent(issue, IsCompletedChanged(isCompleted))
    }

    fun setTitle(issueId: Int, title: String) {
        val issue = transaction(db) {
            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.title] = title
            }

            getIssueInTransaction(issueId) ?: error("Issue not found")
        }

        broadcastIssueModificationEvent(issue, TitleChanged(title))
    }

    fun setAssigneeId(issueId: Int, assigneeId: Int?) {
        val issue = transaction(db) {
            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.assigneeId] = assigneeId
            }

            getIssueInTransaction(issueId) ?: error("Issue not found")
        }

        broadcastIssueModificationEvent(issue, AssigneeIdChanged(assigneeId))
    }
}

typealias IssueChangedCheckedSubscription = CheckedSubscription<Issue, IssueChangedEvent>

interface CheckedSubscription<T, E> {
    fun check(item: T): Boolean
    fun emit(event: E)
}

class FlowSubscription<T, E>(
    private val scope: CoroutineScope,
    private val flow: FlowCollector<E>,
    private val checkItem: (T) -> Boolean,
): CheckedSubscription<T, E> {
    override fun check(item: T) = checkItem(item)

    override fun emit(event: E) {
        scope.launch {
            flow.emit(event)
        }
    }
}
