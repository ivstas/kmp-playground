package org.kmp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC

interface AwesomeApi : RPC {
    suspend fun getNews(city: String): Flow<String>
}