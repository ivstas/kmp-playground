package org.kmp

import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc


@Rpc
interface IssueApi: RemoteService {
    suspend fun addIssue(issueIn: IssueIn): Int
    suspend fun removeIssue(issueId: Int)

    suspend fun getIssues(): List<Issue>
    suspend fun getIssue(issueId: Int): Issue?
    suspend fun setIsCompleted(issueId: Int, isCompleted: Boolean)
    suspend fun setAssigneeId(issueId: Int, assigneeId: Int)
    suspend fun setTitle(issueId: Int, title: String)

    suspend fun subscribeToIssue(issueId: Int): InitializedFlow<Issue, IssueChangedEvent>?
    suspend fun subscribeToAllIssues(): InitializedIssueListUpdates
    suspend fun subscribeToAssigneeIssues(assigneeId: Int): InitializedIssueListUpdates
}

@Rpc
interface UserApi: RemoteService {
    suspend fun getUser(userId: Int): User?
    suspend fun subscribeToAllUsers(): InitializedListUpdates<User, Int, UserChangedEvent>
    suspend fun changeUserName(userId: Int, name: String)
}