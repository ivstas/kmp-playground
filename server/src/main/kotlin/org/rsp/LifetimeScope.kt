package org.rsp

import kotlinx.coroutines.*

fun CoroutineScope.toLifetime(): Lifetime {
    return LifetimeSource().also { source ->
        coroutineContext.job.invokeOnCompletion {
            source.terminate()
        }
    }
}

fun Lifetime.toCoroutineScope(): CoroutineScope {
    val job = Job()

    callWhenTerminated(job::cancel)

    return CoroutineScope(Dispatchers.Default + job)
}

val GlobalLifetime = GlobalScope.toLifetime()