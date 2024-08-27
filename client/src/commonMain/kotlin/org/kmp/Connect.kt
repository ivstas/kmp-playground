package org.kmp

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.KtorRPCClient
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig

internal suspend fun connectToServer(): KtorRPCClient {
    val httpClient = HttpClient {
        installRPC {
            waitForServices = true
        }
    }

    delay(500) // simulate network delay to connection state to be visible

    return httpClient.rpc {
        url {
            host = "localhost"
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
