package org.kmp

import org.kmp.manager.IssueManager
import org.kmp.manager.UserManager
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

    override suspend fun setTitle(issueId: Int, title: String) {
        issueManager.setTitle(issueId, title)
    }

    override suspend fun setIsCompleted(issueId: Int, isCompleted: Boolean) {
        issueManager.setIsCompleted(issueId, isCompleted)
    }

    override suspend fun subscribeToIssue(issueId: Int): InitializedFlow<Issue, IssueChangedEvent>? {
        return issueManager.subscribeToIssue(this, issueId)
    }

    override suspend fun subscribeToAllIssues(): InitializedIssueListUpdates {
        return issueManager.subscribeToAllIssues(this)
    }

    override suspend fun subscribeToSingleIssue(issueId: Int): InitializedIssueListUpdates {
        return issueManager.subscribeToAssigneeIssues(this, issueId)
    }
}

class UserApiHandler(
    override val coroutineContext: CoroutineContext,
    private val userManager: UserManager,
): UserApi {
    override suspend fun getUser(userId: Int): User? {
        return userManager.getUser(userId)
    }
}