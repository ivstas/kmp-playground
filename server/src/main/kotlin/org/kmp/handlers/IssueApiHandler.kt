package org.kmp.handlers

import kotlinx.coroutines.flow.Flow
import org.kmp.Issue
import org.kmp.IssueChangedEvent
import org.kmp.IssueIn
import org.kmp.IssueListModificationEvent
import org.kmp.api.AllIssuesEventFlow
import org.kmp.api.IssueApi
import org.kmp.manager.IssueManager
import org.rsp.GlobalLifetime
import kotlin.coroutines.CoroutineContext

class IssueApiHandler(
    override val coroutineContext: CoroutineContext,
    private val issueManager: IssueManager,
): IssueApi {
    override suspend fun addIssue(issueIn: IssueIn): Long {
        return issueManager.addIssue(issueIn)
    }

    override suspend fun getIssues(): List<Issue> {
        return issueManager.getIssues()
    }

    override suspend fun getIssueEventFlow(): AllIssuesEventFlow {
        // todo: generate subscription id, return it alongside with the flow
        return issueManager.listenToIssues(GlobalLifetime)
    }
}