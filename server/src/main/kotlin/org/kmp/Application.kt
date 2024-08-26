package org.kmp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(RPC)
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        rpc("/awesome") {
            rpcConfig {
                serialization {
                    json()
                }
            }

            registerService<AwesomeService> { ctx -> AwesomeServiceImpl(ctx) }
        }
    }
}