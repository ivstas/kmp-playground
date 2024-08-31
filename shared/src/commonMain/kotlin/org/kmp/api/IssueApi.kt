package org.kmp.api

import kotlinx.rpc.RPC
import org.kmp.domain.IssueIn

interface IssueApi: RPC {
    suspend fun addIssue(issueIn: IssueIn): Long
}