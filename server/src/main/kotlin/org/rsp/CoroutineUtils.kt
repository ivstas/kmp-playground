package org.rsp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun CoroutineScope.createChildScope(): CoroutineScope {
    val childJob = Job(coroutineContext[Job])
    return CoroutineScope(coroutineContext + childJob)
}