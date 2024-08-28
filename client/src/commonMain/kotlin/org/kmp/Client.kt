@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService

@Suppress("unused")
@JsExport
class MessageApi(private val rpcClient: KtorRPCClient) {
    fun listenToMessageFlow(scope: CoroutineScope, collector: (value: String) -> Unit) {
        scope.launch {
            streamScoped {
                rpcClient.withService<AwesomeService>().getNews("KotlinBurg").collect(collector)
            }
        }
    }
}

private var counter = 0

@JsExport
@Suppress("MemberVisibilityCanBePrivate")
class DisposableChecker: Disposable {
    var isDisposed = false
    val uniqueCounter = ++counter

    override fun dispose() {
        if (isDisposed) {
            throw IllegalStateException("Already disposed")
        }
        println("disposed checker $uniqueCounter")

        isDisposed = true
    }
}

@JsExport
fun isScopeActive(scope: CoroutineScope): Boolean = scope.isActive // fixme

@JsExport
class ScopeProxy: Disposable {
    private val job = Job()
    @Suppress("unused")
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun dispose() = job.cancel()
}

@JsExport
interface Disposable {
    fun dispose()
}