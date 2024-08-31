package org.kmp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.manager.IssueManager
import org.kmp.db.tables.IssuesTagsTable
import org.kmp.db.tables.IssuesTable
import org.kmp.db.tables.TagsTable
import org.kmp.db.tables.UsersTable
import org.kmp.api.AwesomeApi
import org.kmp.api.IssueApi
import org.kmp.db.tables.IssuesTable.title
import org.kmp.handlers.AwesomeApiHandler
import org.kmp.handlers.IssueApiHandler
import java.io.File


fun main() {
    // DB_CLOSE_DELAY=-1 is a hack
    // preventing in-memory H2 database from removing all the data when transaction closes the connection
    val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    transaction(db) {
        SchemaUtils.create(UsersTable, IssuesTable, TagsTable, IssuesTagsTable)

        UsersTable.insert {
            it[name] = "John Doe"
        }

        val issuesIn = listOf(
            "Create kmp app using wizard",
            "Add client and web modules",
            "Add rpc",
            "Add database, initialize tables and fill the data",
        ).map(::IssueIn)

        IssuesTable.batchInsert(issuesIn) {
            this[title] = it.title
        }
    }

    val issueService = IssueManager(db)

    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0") {
        install(RPC)
        routing {
            rpc(RPC_PATH) {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<AwesomeApi> { ctx -> AwesomeApiHandler(ctx) }
                registerService<IssueApi> { ctx -> IssueApiHandler(ctx, issueService) }
            }
            staticFiles("/", File("web/dist"))
        }
    }.start(wait = true)
}
