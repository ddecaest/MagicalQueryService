package com.ddecaest.external

class RepositoryModel(private val entities: List<Entity>, private val joins: List<Join>) {

    fun getField(entityName: String, fieldName: String): Field? {
        return getEntity(entityName)?.fields?.find { it -> it.name == fieldName }
    }

    fun getEntity(entityName: String): Entity? {
        return entities.find { it.name == entityName }
    }

    fun getJoinColumns(entityA: String, entityB: String): Join? {
        return joins.find {
            (it.entityA == entityA && it.entityB == entityB) || (it.entityA == entityB && it.entityB == entityA)
        }
    }
}

data class Entity(val name: String, val tableName: String, val fields: Collection<Field>)

data class Field(val name: String, val columnName: String, val type: FieldType)

data class Join(val entityA: String, val entityB: String, val entityAJoinColumn: String, val entityBJoinColumn: String)

enum class FieldType {
    STRING,
    LONG
}