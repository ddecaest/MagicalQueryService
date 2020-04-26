package com.ddecaest.internal

import com.ddecaest.external.RepositoryModel
import org.springframework.jdbc.core.RowMapper
import java.lang.IllegalArgumentException

internal class JdbcQueryBuilder(private val repositoryModel: RepositoryModel) {

    fun build(query: Query): JdbcQuery {

        val entitiesUsed = query.fields.map { it.entityName }.toSet()
        if (entitiesUsed.size > 1) {
            throw IllegalArgumentException("Sorry, I am too stupid to handle queries containing more than one entity for now!")
        }

        val selectClause = query.fields.joinToString(transform = { selectedFieldAsSql(it) }, separator = ",")
        val sql = "SELECT $selectClause FROM ${repositoryModel.entityAsTableName(entitiesUsed.iterator().next())}"

        // TODO params
        // TODO rowMapper
        val rowMapper = RowMapper<Any> { resultSet, rowNum ->
            listOf<String>()
        }
        return JdbcQuery(sql, emptyMap(), rowMapper)
    }

    private fun selectedFieldAsSql(selectedField: SelectedField): String {
        val tableName = entityAsTableName(selectedField.entityName)
        val fieldName = repositoryModel.fieldAsTableName(selectedField.entityName, selectedField.fieldName)
            ?: throw IllegalArgumentException("There is no field ${selectedField.fieldName} mapped to the entity ${selectedField.entityName}")

        return "$fieldName.$tableName"
    }

    private fun entityAsTableName(entityName: String): String {
        return repositoryModel.entityAsTableName(entityName)
            ?: throw IllegalArgumentException("There is no table mapped to the unknown entity $entityName")
    }

    class JdbcQuery(val sql: String, val params: Map<String, Any>, val rowMapper: RowMapper<Any>)
}