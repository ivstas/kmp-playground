package org.kmp.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC
import org.kmp.Wrapper

interface AwesomeApi : RPC {
    suspend fun getNews(city: String): Flow<Wrapper<String>>
}