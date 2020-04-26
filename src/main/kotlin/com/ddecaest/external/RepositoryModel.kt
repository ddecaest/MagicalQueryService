package com.ddecaest.external

class RepositoryModel(private val entities: List<Entity>) {

    private fun getEntity(entityName: String) = entities.find { it.name == entityName }

    fun fieldAsTableName(entityName: String, fieldName: String): String? {
        return getEntity(entityName)?.fields?.get(fieldName)
    }

    fun entityAsTableName(entityName: String): String? {
        return getEntity(entityName)?.tableName
    }
}

class Entity(val name: String, val tableName: TableName, val fields: Map<String, ColumnName>)

typealias TableName = String
typealias ColumnName = String