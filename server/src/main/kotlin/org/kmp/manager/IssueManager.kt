package org.kmp.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.Issue
import org.kmp.IssueChangedEvent
import org.kmp.db.tables.IssuesTable
import org.kmp.IssueIn
import org.kmp.IssueListModificationEvent
import org.kmp.api.AllIssuesEventFlow
import org.rsp.IterableModificationEvent
import org.rsp.IterableModificationEventReset
import org.rsp.Lifetime
import org.rsp.toCoroutineScope


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

    private val allIssueSubscriptions = mutableSetOf<AllIssueSubscription>()

    fun listenToIssues(lifetime: Lifetime): AllIssuesEventFlow {
        val listModificationFlow = MutableSharedFlow<IssueListModificationEvent>()
        val issueChangedFlow = MutableSharedFlow<Pair<Long, IssueChangedEvent>>()

        val subscription = AllIssueSubscription(lifetime, listModificationFlow, issueChangedFlow)

        allIssueSubscriptions.add(subscription)

        lifetime.callWhenTerminated {
            allIssueSubscriptions.remove(subscription)
        }

        lifetime.toCoroutineScope().launch {
            val issues = getIssues()
            listModificationFlow.emit(IterableModificationEventReset(issues.toTypedArray()))
        }

        return AllIssuesEventFlow(listModificationFlow, issueChangedFlow)
    }
}

data class AllIssueSubscription(
    val lifetime: Lifetime,
    val listModificationFlow: MutableSharedFlow<IssueListModificationEvent>,
    val issueChangedFlow: MutableSharedFlow<Pair<Long, IssueChangedEvent>>
)