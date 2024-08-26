package org.kmp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

class AwesomeServiceImpl(override val coroutineContext: CoroutineContext) : AwesomeService {
    override suspend fun getNews(city: String): Flow<String> {
        return flow {
            emit("Today is 23 degrees!")
            emit("Harry Potter is in $city!")
            emit("New dogs cafe has opened doors to all fluffy customers!")
        }
    }
}