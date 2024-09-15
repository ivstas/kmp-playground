@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService
import org.kmp.api.AwesomeApi
import org.kmp.api.IssueApi
import org.rsp.IterableModificationEventReset
import kotlin.js.Promise

@Suppress("unused")
@JsExport
class MessageApi(private val rpcClient: KtorRPCClient) {
    fun listenToMessageFlow(scope: CoroutineScope, collector: (value: String) -> Unit) {
        scope.launch {
            streamScoped {
                val flow = rpcClient.withService<AwesomeApi>().getNews("KotlinBurg")
                flow.collect {
                    collector(it.value)
                }
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

    @OptIn(DelicateCoroutinesApi::class)
    fun getIssue(issueId: Int, scope: CoroutineScope = GlobalScope): Promise<Issue?> {
        return scope.promise {
            rpcClient.withService<IssueApi>().getIssue(issueId)
        }
    }

    fun listenToIssueEvents(
        scope: CoroutineScope,
        issuesModificationEventListener: IssuesModificationEventListener,
    ) {
        scope.launch {
            streamScoped {
                val updates = rpcClient.withService<IssueApi>().getIssueEventFlow()

                launch {
                    updates.elementChangedFlow.collect { (id, event) ->
                        issuesModificationEventListener.getOnElementChangedListener(id).collector(event)
                    }
                }
                updates.listChangedFlow.collect(issuesModificationEventListener.onListChanged::collector)
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