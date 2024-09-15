package org.kmp.api

import kotlinx.rpc.RPC
import org.kmp.Issue
import org.kmp.IssueIn
import org.kmp.IssueListUpdates

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Int
    suspend fun getIssues(): List<Issue>
    suspend fun getIssue(issueId: Int): Issue?
    suspend fun getIssueEventFlow(): IssueListUpdates
}

