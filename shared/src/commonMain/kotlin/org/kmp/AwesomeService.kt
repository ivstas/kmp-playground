package org.kmp

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC

interface AwesomeService : RPC {
    suspend fun getNews(city: String): Flow<String>
}