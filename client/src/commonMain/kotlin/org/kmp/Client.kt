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