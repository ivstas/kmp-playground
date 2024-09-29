@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp
import kotlinx.coroutines.*
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService
import kotlin.js.Promise


@Suppress("unused")
@JsExport
class IssueApiWrapper(private val rpcClient: KtorRPCClient) {
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

    fun setIsCompleted(issueId: Int, isCompleted: Boolean, scope: CoroutineScope = GlobalScope): Promise<Unit> {
        return scope.promise {
            rpcClient.withService<IssueApi>().setIsCompleted(issueId, isCompleted)
        }
    }
}

@Suppress("unused")
@JsExport
class UserApiWrapper(private val rpcClient: KtorRPCClient) {
    @OptIn(DelicateCoroutinesApi::class)
    fun getUser(userId: Int, scope: CoroutineScope = GlobalScope): Promise<User?> {
        return scope.promise {
            rpcClient.withService<UserApi>().getUser(userId)
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