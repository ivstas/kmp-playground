package org.kmp

import kotlinx.rpc.RPC

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Int
    suspend fun getIssues(): List<Issue>
    suspend fun getIssue(issueId: Int): Issue?
    suspend fun getIssueEventFlow(): IssueListUpdates
}

interface UserApi: RPC {
    suspend fun getUser(userId: Int): User?
}