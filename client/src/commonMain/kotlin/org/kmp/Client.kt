@file:Suppress("NON_EXPORTABLE_TYPE")

package org.kmp

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.withService
import org.rsp.IterableModificationEventAdded
import org.rsp.IterableModificationEventRemoved
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

    @OptIn(DelicateCoroutinesApi::class)
    fun removeIssue(issueId: Int, scope: CoroutineScope = GlobalScope): Promise<Unit> {
        return scope.promise {
            rpcClient.withService<IssueApi>().removeIssue(issueId)
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

    fun subscribeToAllIssues(
        scope: CoroutineScope
    ): Promise<InitializedEventFlow<List<Issue>>> {
        return Promise { resolve, reject ->
            scope.launch {
                streamScoped {
                    val initializedIssueListUpdates = try {
                        rpcClient.withService<IssueApi>().subscribeToAllIssues()
                    } catch (e: Throwable) {
                        reject(e)
                        return@streamScoped // ends the subscription
                    }

                    launch {
                        resolve(InitializedIssueListEventFlow(this@streamScoped, initializedIssueListUpdates))
                    }

                    awaitCancellation() // keeps the subscription alive
                }
            }
        }
    }

    fun subscribeToAssigneeIssues(
        scope: CoroutineScope,
        assigneeId: Int,
    ): Promise<InitializedEventFlow<List<Issue>>> {
        return Promise { resolve, reject ->
            scope.launch {
                streamScoped {
                    val initializedIssueListUpdates = try {
                        rpcClient.withService<IssueApi>().subscribeToAssigneeIssues(assigneeId)
                    } catch (e: Throwable) {
                        reject(e)
                        return@streamScoped // ends the subscription
                    }

                    launch {
                        resolve(InitializedIssueListEventFlow(this@streamScoped, initializedIssueListUpdates))
                    }

                    awaitCancellation() // keeps the subscription alive
                }
            }
        }
    }

    fun subscribeToIssue(
        scope: CoroutineScope,
        issueId: Int,
    ): Promise<InitializedEventFlow<Issue>> {
        return Promise { resolve, reject ->
            scope.launch {
                streamScoped {
                    val initializedFlow = try {
                        rpcClient.withService<IssueApi>().subscribeToIssue(issueId)
                            ?: throw Exception("Issue not found")
                    } catch (e: Throwable) {
                        reject(e)
                        return@streamScoped // ends the subscription
                    }

                    val initializedIssueCollector = InitializedIssueCollector(initializedFlow.initialValue, initializedFlow.flow, this)
                    launch {
                        resolve(initializedIssueCollector)
                    }

                    awaitCancellation() // keeps the subscription alive
                }
            }
        }
    }

    fun setTitle(issueId: Int, title: String, scope: CoroutineScope = GlobalScope): Promise<Unit> {
        return scope.promise {
            rpcClient.withService<IssueApi>().setTitle(issueId, title)
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
class ScopeProxy : Disposable {
    private val job = Job()

    @Suppress("unused")
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun dispose() = job.cancel()
}

@JsExport
interface Disposable {
    fun dispose()
}

@JsExport
interface InitializedEventFlow<T> {
    val initialValue: T
    fun listenToUpdates(update: (mapper: (T) -> T) -> Unit)
}


class InitializedIssueListEventFlow(
    private val scope: CoroutineScope,
    private val initializedIssueListUpdates: InitializedIssueListUpdates,
): InitializedEventFlow<List<Issue>> {
    override val initialValue = initializedIssueListUpdates.initialValue
    override fun listenToUpdates(update: (mapper: (List<Issue>) -> List<Issue>) -> Unit) {
        scope.launch {
            initializedIssueListUpdates.listChangedFlow.collect { listModificationEvent ->
                update { current ->
                    when (listModificationEvent) {
                        is IterableModificationEventAdded -> current + listModificationEvent.item
                        is IterableModificationEventRemoved -> current.filter { it.id != listModificationEvent.id }
                    }
                }
            }
        }
        scope.launch {
            initializedIssueListUpdates.elementChangedFlow.collect { (id, event) ->
                update { current ->
                    current.map { issue ->
                        if (issue.id == id) {
                            issue.updateWith(event)
                        } else {
                            issue
                        }
                    }
                }
            }
        }
    }
}

class InitializedIssueCollector(
    override val initialValue: Issue,
    private val flow: Flow<IssueChangedEvent>,
    private val scope: CoroutineScope,
): InitializedEventFlow<Issue> {
    override fun listenToUpdates(update: (mapper: (Issue) -> Issue) -> Unit) {
        scope.launch {
            flow.collect { event ->
                update { current ->
                    current.updateWith(event)
                }
            }
        }
    }
}

private fun Issue.updateWith(event: IssueChangedEvent) = when (event) {
    is TitleChanged -> copy(title = event.title)
    is IsCompletedChanged -> copy(isCompleted = event.isCompleted)
    is AssigneeIdChanged -> copy(assigneeId = event.assigneeId)
}
