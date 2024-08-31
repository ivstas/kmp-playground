package org.kmp.db.tables

import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

const val MAX_VARCHAR_LENGTH = 128

object UsersTable : LongIdTable() {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
}

object IssuesTable : LongIdTable() {
    val title = varchar("name", MAX_VARCHAR_LENGTH)
    val assigneeId = long("assignee_id").references(UsersTable.id).nullable()
    val isCompleted = bool("completed").default(false)
}

object TagsTable : LongIdTable() {
    val title = varchar("title", MAX_VARCHAR_LENGTH)
    val creatorIdTable = long("creator_id").references(UsersTable.id)
}

object IssuesTagsTable : CompositeIdTable() {
    val issueId = long("issue_id").references(IssuesTable.id)
    val tagId = long("tag_id").references(TagsTable.id)

    override val primaryKey = PrimaryKey(issueId, tagId)
}