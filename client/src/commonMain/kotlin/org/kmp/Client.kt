@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService
import org.kmp.api.AwesomeApi
import org.kmp.api.IssueApi
import kotlin.js.Promise

@Suppress("unused")
@JsExport
class MessageApi(private val rpcClient: KtorRPCClient) {
    fun listenToMessageFlow(scope: CoroutineScope, collector: (value: String) -> Unit) {
        scope.launch {
            streamScoped {
                rpcClient.withService<AwesomeApi>().getNews("KotlinBurg").collect(collector)
            }
        }
    }
}

@Suppress("unused")
@JsExport
class IssueApi(private val rpcClient: KtorRPCClient) {
    @OptIn(DelicateCoroutinesApi::class)
    fun addIssue(issueIn: IssueIn, scope: CoroutineScope = GlobalScope): Promise<Int> {
        return scope.promise {
            rpcClient.withService<IssueApi>().addIssue(issueIn)
        }
    }

    fun getIssues(scope: CoroutineScope): Promise<Array<Issue>> {
        return scope.promise {
            rpcClient.withService<IssueApi>().getIssues().toTypedArray()
        }
    }

    fun listenToIssueEvents(
        scope: CoroutineScope,
        issuesModificationEventListener: IssuesModificationEventListener,
    ) {
        scope.promise {
            streamScoped {
                val flow = rpcClient.withService<IssueApi>().getIssueEventFlow()

                launch {
                    flow.collect(issuesModificationEventListener::collector)
                }
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