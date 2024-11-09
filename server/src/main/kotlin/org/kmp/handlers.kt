@file:OptIn(ExperimentalRPCApi::class)

package org.kmp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.rpc.internal.utils.ExperimentalRPCApi
import kotlinx.rpc.krpc.invokeOnStreamScopeCompletion
import org.kmp.manager.IssueManager
import org.kmp.manager.UserManager
import org.rsp.Lifetime
import org.rsp.createChildScope
import kotlin.coroutines.CoroutineContext

class IssueApiHandler(
    override val coroutineContext: CoroutineContext,
    private val issueManager: IssueManager,
): IssueApi {
    override suspend fun addIssue(issueIn: IssueIn): Int {
        return issueManager.addIssue(issueIn)
    }

    override suspend fun removeIssue(issueId: Int) {
        issueManager.removeIssue(issueId)
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

    override suspend fun setAssigneeId(issueId: Int, assigneeId: Int) {
        issueManager.setAssigneeId(issueId, assigneeId)
    }

    override suspend fun subscribeToIssue(issueId: Int): InitializedFlow<Issue, IssueChangedEvent>? {
        return withChildScope {
            issueManager.subscribeToIssue(it, issueId)
        }
    }

    override suspend fun subscribeToAllIssues(): InitializedIssueListUpdates {
        return issueManager.subscribeToAllIssues(this)
    }

    @OptIn(ExperimentalRPCApi::class)
    private suspend inline fun <T> withChildScope(block: (CoroutineScope) -> T): T {
        val childScope = createChildScope()

        invokeOnStreamScopeCompletion {
            childScope.cancel()
        }

        return block(childScope)
    }

    override suspend fun subscribeToAssigneeIssues(assigneeId: Int): InitializedIssueListUpdates {
        return withChildScope {
            issueManager.subscribeToAssigneeIssues(it, assigneeId)
        }
    }
}



class UserApiHandler(
    override val coroutineContext: CoroutineContext,
    private val userManager: UserManager,
): UserApi {
    override suspend fun getUser(userId: Int): User? {
        return userManager.getUser(userId)
    }

    override suspend fun changeUserName(userId: Int, name: String) {
        userManager.setName(userId, name)
    }

    override suspend fun subscribeToAllUsers(): InitializedListUpdates<User, Int, UserChangedEvent> {
        return userManager.subscribeToAllUsers(this)
    }
}