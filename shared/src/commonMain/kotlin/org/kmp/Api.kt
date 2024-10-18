package org.kmp

import kotlinx.rpc.RPC

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Int
    suspend fun getIssues(): List<Issue>
    suspend fun getIssue(issueId: Int): Issue?
    suspend fun setIsCompleted(issueId: Int, isCompleted: Boolean)
    suspend fun setTitle(issueId: Int, title: String)

    suspend fun subscribeToIssue(issueId: Int): InitializedFlow<Issue, IssueChangedEvent>?
    suspend fun subscribeToAllIssues(): InitializedIssueListUpdates
    suspend fun subscribeToAssigneeIssues(assigneeId: Int): InitializedIssueListUpdates
}

interface UserApi: RPC {
    suspend fun getUser(userId: Int): User?
}