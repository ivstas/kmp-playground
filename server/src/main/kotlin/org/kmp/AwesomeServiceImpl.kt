package org.kmp

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

class AwesomeServiceImpl(override val coroutineContext: CoroutineContext) : AwesomeService {
    override suspend fun getNews(city: String) = flow {
        delay(500)
        emit("Today is 23 degrees!")
        delay(500)
        emit("Harry Potter is in $city!")
        delay(500)
        emit("New dogs cafe has opened doors to all fluffy customers!")
    }
}