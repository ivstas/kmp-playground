package org.kmp

import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

const val MAX_VARCHAR_LENGTH = 128

object UsersTable : IntIdTable() {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
}

object IssuesTable : IntIdTable() {
    val title = varchar("name", MAX_VARCHAR_LENGTH)
    val assigneeId = integer("assignee_id").references(UsersTable.id).nullable()
    val isCompleted = bool("completed").default(false)
}

object TagsTable : IntIdTable() {
    val title = varchar("title", MAX_VARCHAR_LENGTH)
//    val creatorIdTable = integer("creator_id").references(UsersTable.id)
}

object IssuesTagsTable : CompositeIdTable() {
    val issueId = integer("issue_id").references(IssuesTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = integer("tag_id").references(TagsTable.id)

    override val primaryKey = PrimaryKey(issueId, tagId)
}