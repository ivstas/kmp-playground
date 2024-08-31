package org.kmp.handlers

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.kmp.api.AwesomeApi
import kotlin.coroutines.CoroutineContext

class AwesomeApiHandler(override val coroutineContext: CoroutineContext) : AwesomeApi {
    override suspend fun getNews(city: String) = flow {
        delay(500)
        emit("Today is 23 degrees!")
        delay(500)
        emit("Harry Potter is in $city!")
        delay(500)
        emit("New dogs cafe has opened doors to all fluffy customers!")
    }
}