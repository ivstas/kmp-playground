package org.kmp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC
import org.kmp.Issue
import org.kmp.IssueChangedEvent
import org.kmp.IssueIn
import org.kmp.IssueListModificationEvent

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Long
    suspend fun getIssues(): List<Issue>
    suspend fun getIssueEventFlow(): AllIssuesEventFlow
}

data class AllIssuesEventFlow(
    val listModificationFlow: Flow<IssueListModificationEvent>,
    val issueChangedFlow: Flow<Pair<Long, IssueChangedEvent>>,
)
