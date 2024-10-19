package org.kmp

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.server.RPC
import kotlinx.rpc.transport.ktor.server.rpc
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.manager.IssueManager
import org.kmp.manager.UserManager


fun main() {
    // DB_CLOSE_DELAY=-1 is a hack
    // preventing in-memory H2 database from removing all the data when transaction closes the connection
    val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    fillTestData(db)

    val issueService = IssueManager(db)
    val userService = UserManager(db)

    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0") {
        install(RPC)
        routing {
            rpc(RPC_PATH) {
                rpcConfig {
                    serialization {
                        json()
                    }
                }

                registerService<IssueApi> { ctx -> IssueApiHandler(ctx, issueService) }
                registerService<UserApi> { ctx -> UserApiHandler(ctx, userService) }
            }
        }
    }.start(wait = true)
}

private fun fillTestData(db: Database) {
    transaction(db) {
        SchemaUtils.create(UsersTable, IssuesTable, TagsTable, IssuesTagsTable)

        val alice = User(1, "Alice Kim")
        val john = User(2, "John Doe")
        val alexander = User(3, "Alexander Smith")
        val cassandra = User(4, "Cassandra Brown")

        UsersTable.batchInsert(
            listOf(
                alice,
                john,
                alexander,
                cassandra,
            )
        ) {
            this[UsersTable.name] = it.name
        }

        val issues = listOf(
            IssueIn("Create kmp app using wizard", alice.id, true),
            IssueIn("Add client and web modules", john.id, true),
            IssueIn("Add rpc", john.id),
            IssueIn("Add database, initialize tables and fill the data", alexander.id),
            IssueIn("Login page throws error", john.id),
            IssueIn("Add search functionality", alice.id),
            IssueIn("Update user guide", cassandra.id),
            IssueIn("Slow page loading times", alexander.id),
        )

        IssuesTable.batchInsert(issues) {
            this[IssuesTable.title] = it.title
            this[IssuesTable.assigneeId] = it.assigneeId
            this[IssuesTable.isCompleted] = it.isCompleted
        }

        TagsTable.batchInsert(listOf(
            TagIn("bug"),
            TagIn("feature"),
            TagIn("setup"),
            TagIn("documentation"),
        )) {
            this[TagsTable.title] = it.title
        }

        IssuesTagsTable.batchInsert(
            listOf(
                1 to 3,
                2 to 3,
                3 to 3,
                4 to 3,
                5 to 1,
                6 to 2,
                7 to 4,
                8 to 1,
            )
        ) { (issue, tag) ->
            this[IssuesTagsTable.issueId] = issue
            this[IssuesTagsTable.tagId] = tag
        }
    }
}
