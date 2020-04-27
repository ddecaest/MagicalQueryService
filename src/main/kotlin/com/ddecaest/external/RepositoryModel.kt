package com.ddecaest.external

class RepositoryModel(private val entities: List<Entity>) {

    fun getField(entityName: String, fieldName: String): Field? {
        return getEntity(entityName)?.fields?.find { it -> it.name == fieldName }
    }

    fun getEntity(entityName: String): Entity? {
        return entities.find { it.name == entityName }
    }
}

data class Entity(val name: String, val tableName: String, val fields: Collection<Field>)

data class Field(val name: String, val columnName: String, val type: FieldType)

enum class FieldType {
    STRING,
    LONG
}