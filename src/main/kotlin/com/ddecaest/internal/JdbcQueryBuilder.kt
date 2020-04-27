package com.ddecaest.internal

import com.ddecaest.external.Entity
import com.ddecaest.external.Field
import com.ddecaest.external.FieldType
import com.ddecaest.external.RepositoryModel
import org.springframework.jdbc.core.RowMapper
import java.lang.IllegalArgumentException

internal class JdbcQueryBuilder(private val repositoryModel: RepositoryModel) {

    fun build(query: Query): JdbcQuery {

        val entitiesUsed = query.fields.map { it.entityName }.toSet()
        if (entitiesUsed.size > 1) {
            throw IllegalArgumentException("Sorry, I am too stupid to handle queries containing more than one entity for now!")
        }

        val sql = buildSql(query, entitiesUsed)
        val params = buildParams()
        val rowMapper = buildRowMapper(query)

        return JdbcQuery(sql, params, rowMapper)
    }

    private fun buildSql(query: Query, entitiesUsed: Set<String>): String {
        val entityUsed = getEntity(entitiesUsed.iterator().next())
        val selectClause = query.fields.joinToString(transform = { selectedFieldAsSql(it) }, separator = ",")
        return "SELECT $selectClause FROM ${entityUsed.tableName}"
    }

    private fun buildParams(): Map<String, Any> {
        // Add this when it becomes relevant
        return mapOf<String, Any>()
    }

    private fun buildRowMapper(query: Query): RowMapper<Any> {
        // TODO: what if a column name appears in 2 entities?
        return RowMapper { resultSet, _ ->
            val result = mutableMapOf<String, Any>()

            for (field in query.fields) {
                val fieldInModel = getField(field)
                @Suppress("IMPLICIT_CAST_TO_ANY")
                val variableResult = when (fieldInModel.type) {
                    FieldType.STRING -> resultSet.getString(fieldInModel.columnName)
                    FieldType.LONG -> resultSet.getLong(fieldInModel.columnName)
                }
                result[fieldInModel.name] = variableResult
            }

            result
        }
    }

    private fun selectedFieldAsSql(selectedField: SelectedField): String {
        val fieldName = getField(selectedField).columnName
        val tableName = getEntity(selectedField.entityName).tableName

        return "$tableName.$fieldName"
    }

    private fun getField(selectedField: SelectedField): Field {
        return (repositoryModel.getField(selectedField.entityName, selectedField.fieldName)
            ?: throw IllegalArgumentException("There is no field ${selectedField.fieldName} mapped to the entity ${selectedField.entityName}"))
    }

    private fun getEntity(entityName: String): Entity {
        return repositoryModel.getEntity(entityName)
            ?: throw IllegalArgumentException("There is no table mapped to the unknown entity $entityName")
    }


    class JdbcQuery(val sql: String, val params: Map<String, Any>, val rowMapper: RowMapper<Any>)
}