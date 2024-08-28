package org.kmp

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.promise
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import kotlin.js.Promise

@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
fun connectToServerPromise(scope: CoroutineScope): Promise<KtorRPCClient> {
    return scope.promise {
        connectToServer()
    }
}

private suspend fun connectToServer(): KtorRPCClient {
    val httpClient = HttpClient {
        installRPC {
            waitForServices = true
        }
    }

    delay(500) // simulate network delay to connection state to be visible

    return httpClient.rpc {
        url {
            host = window.location.hostname
            port = SERVER_PORT
            encodedPath = RPC_PATH
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }
}
