package org.kmp
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.rpc.withService
import kotlinx.rpc.serialization.json
import kotlinx.rpc.streamScoped
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig

@JsExport
fun shareServerPort(): Int {
    return SERVER_PORT
}

@JsExport
fun printServerFlow() {
    GlobalScope.launch {
        try {
            connect()
        } catch (e: Exception) {
            println("Error: $e")
        }
    }
}

private suspend fun connect() {
    val rpcClient = HttpClient {
        installRPC {
            waitForServices = true
        }
    }.rpc {
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

    streamScoped {
        rpcClient.withService<AwesomeService>().getNews("KotlinBurg").collect { article ->
            println(article)
        }
    }

}