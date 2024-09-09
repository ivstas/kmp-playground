package org.kmp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC
import org.kmp.*

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Int
    suspend fun getIssues(): List<Issue>
    suspend fun getIssueEventFlow(): Flow<IssuesModificationEvent>
}

