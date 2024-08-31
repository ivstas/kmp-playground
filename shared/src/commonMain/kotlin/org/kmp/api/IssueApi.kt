package org.kmp.api

import kotlinx.rpc.RPC
import org.kmp.Issue
import org.kmp.IssueIn

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Long
    suspend fun getIssues(): List<Issue>
}