package org.kmp.handlers

import kotlinx.coroutines.GlobalScope
import org.kmp.Issue
import org.kmp.IssueIn
import org.kmp.IssueListUpdates
import org.kmp.api.IssueApi
import org.kmp.manager.IssueManager
import kotlin.coroutines.CoroutineContext

class IssueApiHandler(
    override val coroutineContext: CoroutineContext,
    private val issueManager: IssueManager,
): IssueApi {
    override suspend fun addIssue(issueIn: IssueIn): Int {
        return issueManager.addIssue(issueIn)
    }

    override suspend fun getIssues(): List<Issue> {
        return issueManager.getIssues()
    }

    override suspend fun getIssue(issueId: Int): Issue? {
        return issueManager.getIssue(issueId)
    }

    override suspend fun getIssueEventFlow(): IssueListUpdates {
        // todo: generate subscription id, return it alongside with the flow
        return issueManager.listenToIssues(GlobalScope)
    }
}