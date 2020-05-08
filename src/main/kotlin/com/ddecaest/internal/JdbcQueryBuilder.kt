package com.ddecaest.internal

import com.ddecaest.external.FieldInterceptor
import com.ddecaest.external.FieldType
import com.ddecaest.external.RepositoryModel
import org.springframework.jdbc.core.RowMapper

internal class JdbcQueryBuilder(private val repositoryModel: RepositoryModel) {

    fun build(query: QueryParser.ParsedQuery, fieldInterceptors: List<FieldInterceptor<Any>>): JdbcQuery {
        val queryModel = buildQueryModel(query)

        val sql = buildSql(queryModel)
        val params = mapOf<String, Any>() // TODO when where clause is implemented
        val rowMapper = buildRowMapper(queryModel, fieldInterceptors)

        return JdbcQuery(sql, params, rowMapper)
    }

    private fun buildQueryModel(query: QueryParser.ParsedQuery): QueryModel {
        val queryModel = QueryModel(repositoryModel)
        query.fieldsSelected.forEach(queryModel::addToQueryModel)
        return queryModel
    }

    private fun buildSql(queryModel: QueryModel): String {
        val selectClause = buildSelectClause(queryModel)
        val fromClause = buildFromClause(queryModel)
        return "$selectClause $fromClause"
    }

    private fun buildSelectClause(queryModel: QueryModel): String {
        return "SELECT " + queryModel.selection.joinToString(separator = ",") { "${it.entityAlias}.${it.columnName} AS ${it.columnAlias}" }
    }

    private fun buildFromClause(queryModel: QueryModel): String {
        val rootNode = queryModel.entityTree!!

        val baseFromAsString = "FROM ${rootNode.tableName} AS ${rootNode.aliasName}"
        val joinsAsString = buildJoins(rootNode).joinToString(separator = " ")
        return "$baseFromAsString $joinsAsString"
    }

    private fun buildJoins(rootNode: QueryModel.EntityNode): List<String> {
        val joins = mutableListOf<String>()

        rootNode.entitiesJoined.forEach {
            val joinedEntity = it.joined
            joins.add("JOIN ${joinedEntity.tableName} AS ${joinedEntity.aliasName} ON ${rootNode.aliasName}.${it.originColumn} = ${joinedEntity.aliasName}.${it.joinedColumn}")
            joins.addAll(buildJoins(it.joined))
        }

        return joins
    }

    private fun buildRowMapper(
        queryModel: QueryModel, fieldInterceptors: List<FieldInterceptor<Any>>
    ): RowMapper<Any> {
        return RowMapper { resultSet, _ ->
            val result = mutableMapOf<String, Any>()

            for (field in queryModel.selection) {
                @Suppress("IMPLICIT_CAST_TO_ANY")
                val variableResult = when (field.type) {
                    FieldType.STRING -> resultSet.getString(field.columnAlias)
                    FieldType.LONG -> resultSet.getLong(field.columnAlias)
                }

                val fieldInterceptor = fieldInterceptors.find { it.entityName == field.entityName && it.fieldName == field.fieldName }
                if(fieldInterceptor != null) {
                    result[field.columnAlias] = fieldInterceptor.transformField(variableResult)
                } else {
                    result[field.columnAlias] = variableResult
                }
            }
            result
        }
    }


    class JdbcQuery(val sql: String, val params: Map<String, Any>, val rowMapper: RowMapper<Any>)
}