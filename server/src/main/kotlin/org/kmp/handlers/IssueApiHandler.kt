package org.kmp.handlers

import org.kmp.Issue
import org.kmp.IssueIn
import org.kmp.api.IssueApi
import org.kmp.manager.IssueManager
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
}