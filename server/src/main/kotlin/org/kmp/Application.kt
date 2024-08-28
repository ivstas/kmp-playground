package org.kmp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import java.io.File

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0") {
        install(RPC)
        routing {
            rpc(RPC_PATH) {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<AwesomeService> { ctx -> AwesomeServiceImpl(ctx) }
            }
            staticFiles("/", File("web/dist"))
        }
    }.start(wait = true)
}
