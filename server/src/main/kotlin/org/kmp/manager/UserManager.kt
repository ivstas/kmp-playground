package org.kmp.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.kmp.*
import org.rsp.IterableModificationEvent
import org.rsp.IterableModificationEventAdded
import org.rsp.IterableModificationEventRemoved


class UserManager(private val db: Database) {
    private val userAdditionRemovalSubscription = mutableSetOf<AddedRemovedSubscription<User>>()
    private val userChangeSubscriptions = mutableSetOf<CheckedSubscription<User, UserChangedEvent>>()

    fun subscribeToAllUsers(scope: CoroutineScope): InitializedListUpdates<User, Int, UserChangedEvent> {
        val users = getAllUsers()

        val listChangedFlow = MutableSharedFlow<IterableModificationEvent<Int, User>>()

        userAdditionRemovalSubscription.add { isAdded, user ->
            val event = if (isAdded) {
                IterableModificationEventAdded(user.id, user)
            } else {
                IterableModificationEventRemoved(user.id)
            }

            scope.launch {
                listChangedFlow.emit(event)
            }
        }

        val elementChangedFlow = MutableSharedFlow<Pair<Int, UserChangedEvent>>()

        userChangeSubscriptions.add(object : CheckedSubscription<User, UserChangedEvent> {
            override fun emit(beforeModification: User, modificationEvent: UserChangedEvent) {
                scope.launch {
                    elementChangedFlow.emit(beforeModification.id to modificationEvent)
                }
            }
        })

        return InitializedListUpdates(users, listChangedFlow, elementChangedFlow)
    }

    fun setName(userId: Int, name: String) {
        val user = transaction(db) {
            val userBeforeModification = getUserInTransaction(userId) ?: error("User not found")
            // todo: update + select
            UsersTable.update(where = { UsersTable.id eq userId }) {
                it[UsersTable.name] = name
            }

            userBeforeModification
        }

        userChangeSubscriptions.forEach { it.emit(user, UserChangedEvent.NameChanged(name)) }
    }

    fun addUser(userName: String): Int {
        val insertStatement = transaction(db) {
            UsersTable.insert {
                it[name] = userName
            }
        }

        val user = insertStatement.let {
            User(
                id = it[UsersTable.id].value,
                name = it[UsersTable.name]
            )
        }

        userAdditionRemovalSubscription.forEach { it(true, user) }

        return user.id
    }

    fun getUser(userId: Int): User? = transaction(db) {
        getUserInTransaction(userId)
    }

    private fun getUserInTransaction(userId: Int) = UsersTable
        .selectAll()
        .where {
            UsersTable.id eq userId
        }
        .map(ResultRow::toUser)
        .firstOrNull()

    private fun getAllUsers() = transaction(db) {
        UsersTable
            .selectAll()
            .map(ResultRow::toUser)
    }
}

private fun ResultRow.toUser() = User(
    id = this[UsersTable.id].value,
    name = this[UsersTable.name],
)
