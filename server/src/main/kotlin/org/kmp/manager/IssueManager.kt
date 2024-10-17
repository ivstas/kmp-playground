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
    fun addIssue(issueIn: IssueIn): Int {
        val entity = transaction(db) {
            IssuesTable.insert {
                it[title] = issueIn.title
            }
        }

        val issue = entity.let {
            Issue(
                id = it[IssuesTable.id].value,
                title = it[IssuesTable.title],
                assigneeId = it[IssuesTable.assigneeId],
                isCompleted = it[IssuesTable.isCompleted]
            )
        }

        issueAdditionRemovalSubscription.forEach { it(true, issue) }

        return issue.id
    }

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

    private val issueAdditionRemovalSubscription = mutableSetOf<AddedRemovedSubscription<Issue>>()

    // subscriptions to track field change events
    private val issueChangeSubscriptions = mutableSetOf<IssueChangedCheckedSubscription>()
    private fun broadcastIssueModificationEvent(entity: Issue, event: IssueChangedEvent) {
        issueChangeSubscriptions.forEach { subscription ->
            subscription.emit(entity, event)
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

    fun subscribeToAllIssues(scope: CoroutineScope): InitializedIssueListUpdates {
        val issues = getIssues()

        val listChangedFlow = MutableSharedFlow<IterableModificationEvent<Int, Issue>>()

        issueAdditionRemovalSubscription.add { isAdded: Boolean, issue: Issue ->
            val event = if (isAdded) {
                IterableModificationEventAdded<Issue>(issue)
            } else {
                IterableModificationEventRemoved<Int>(issue.id)
            }

            scope.launch {
                listChangedFlow.emit(event)
            }
        }

        val elementChangedFlow = MutableSharedFlow<Pair<Int, IssueChangedEvent>>()

        issueChangeSubscriptions.add(object : IssueChangedCheckedSubscription {
            override fun emit(beforeModification: Issue, modificationEvent: IssueChangedEvent) {
                scope.launch {
                    elementChangedFlow.emit(beforeModification.id to modificationEvent)
                }
            }
        })

        return InitializedIssueListUpdates(issues, listChangedFlow, elementChangedFlow)
    }

    fun subscribeToAssigneeIssues(scope: CoroutineScope, assigneeId: Int): InitializedIssueListUpdates {
        val assigneeIssues = transaction(db) {
            IssuesTable
                .selectAll()
                .where { IssuesTable.assigneeId eq assigneeId }
                .map {
                    Issue(
                        id = it[IssuesTable.id].value,
                        title = it[IssuesTable.title],
                        assigneeId = it[IssuesTable.assigneeId],
                        isCompleted = it[IssuesTable.isCompleted]
                    )
                }
        }

        val listChangedFlow = MutableSharedFlow<IterableModificationEvent<Int, Issue>>()

        issueAdditionRemovalSubscription.add { isAdded: Boolean, issue: Issue ->
            if (issue.assigneeId == assigneeId) {
                val event = if (isAdded) {
                    IterableModificationEventAdded<Issue>(issue)
                } else {
                    IterableModificationEventRemoved<Int>(issue.id)
                }

                scope.launch {
                    listChangedFlow.emit(event)
                }
            }
        }

        val elementChangedFlow = MutableSharedFlow<Pair<Int, IssueChangedEvent>>()

        issueChangeSubscriptions.add(object : IssueChangedCheckedSubscription {
            override fun emit(beforeModification: Issue, modificationEvent: IssueChangedEvent) {
                if (modificationEvent is AssigneeIdChanged) {
                    when {
                        beforeModification.assigneeId != assigneeId && modificationEvent.assigneeId == assigneeId -> {
                            // element added
                            scope.launch {
                                val afterModification = beforeModification.copy(assigneeId = modificationEvent.assigneeId)
                                listChangedFlow.emit(IterableModificationEventAdded(afterModification))
                            }
                        }
                        beforeModification.assigneeId == assigneeId && modificationEvent.assigneeId != assigneeId -> {
                            // element removed
                            scope.launch {
                                listChangedFlow.emit(IterableModificationEventRemoved(beforeModification.id))
                            }
                        }
                    }
                } else {
                    // assignee didn't change, can trust beforeModification
                    if (beforeModification.assigneeId == assigneeId) {
                        scope.launch {
                            elementChangedFlow.emit(beforeModification.id to modificationEvent)
                        }
                    }
                }
            }
        })

        return InitializedIssueListUpdates(assigneeIssues, listChangedFlow, elementChangedFlow)
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
            val issueBeforeModification = getIssueInTransaction(issueId) ?: error("Issue not found")
            // todo: update + select
            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.isCompleted] = isCompleted
            }

            issueBeforeModification
        }

        // todo: list events (filter by isCompleted)

        broadcastIssueModificationEvent(issue, IsCompletedChanged(isCompleted))
    }

    fun setTitle(issueId: Int, title: String) {
        val issue = transaction(db) {
            val issueBeforeModification = getIssueInTransaction(issueId) ?: error("Issue not found")

            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.title] = title
            }

            issueBeforeModification
        }

        broadcastIssueModificationEvent(issue, TitleChanged(title))
    }

    fun setAssigneeId(issueId: Int, assigneeId: Int?) {
        val issue = transaction(db) {
            val issueBeforeModification = getIssueInTransaction(issueId) ?: error("Issue not found")
            IssuesTable.update(where = { IssuesTable.id eq issueId }) {
                it[IssuesTable.assigneeId] = assigneeId
            }

            issueBeforeModification
        }

        broadcastIssueModificationEvent(issue, AssigneeIdChanged(assigneeId))
    }
}

typealias AddedRemovedSubscription<T> = (isAdded: Boolean, issue: T) -> Unit

typealias IssueChangedCheckedSubscription = CheckedSubscription<Issue, IssueChangedEvent>

interface CheckedSubscription<T, E> {
    fun emit(beforeModification: T, modificationEvent: E)
}

class FlowSubscription<T, E>(
    private val scope: CoroutineScope,
    private val flow: FlowCollector<E>,
    private val checkItem: (T) -> Boolean,
): CheckedSubscription<T, E> {
    override fun emit(beforeModification: T, modificationEvent: E) {
        if (checkItem(beforeModification)) {
            scope.launch {
                flow.emit(modificationEvent)
            }
        }
    }
}
