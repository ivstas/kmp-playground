@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService

@JsExport
class Client {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    val rpcClient = scope.promise {
        connectToServer()
    }

    fun close() = job.cancel()
}

@JsExport
class Api(private val rpcClient: KtorRPCClient) {
    fun listenToMessageFlow(collector: (value: String) -> Unit) {
        rpcClient.launch {
            streamScoped {
                rpcClient.withService<AwesomeService>().getNews("KotlinBurg").collect(collector)
            }
        }
    }
}