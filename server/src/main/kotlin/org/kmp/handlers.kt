package org.kmp

import kotlinx.coroutines.GlobalScope
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

    override suspend fun getIssueEventFlow(): IssueListUpdates {
        // todo: generate subscription id, return it alongside with the flow
        return issueManager.listenToIssues(GlobalScope)
    }

    override suspend fun setIsCompleted(issueId: Int, isCompleted: Boolean) {
        issueManager.setIsCompleted(issueId, isCompleted)
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