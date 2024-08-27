package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.withService

@JsExport
class Client {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    val api = scope.promise<Api> {
        val rpcClient = connectToServer()

        object: Api {
            override fun listenToMessageFlow(collector: (value: String) -> Unit) {
                rpcClient.launch {
                    streamScoped {
                        rpcClient.withService<AwesomeService>().getNews("KotlinBurg").collect(collector)
                    }
                }
            }
        }
    }

    fun close() = job.cancel()
}

@JsExport
interface Api {
    fun listenToMessageFlow(collector: (value: String) -> Unit)
}