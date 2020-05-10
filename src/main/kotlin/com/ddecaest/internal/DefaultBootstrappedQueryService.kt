package com.ddecaest.internal

import com.ddecaest.external.BootstrappedQueryService
import com.ddecaest.external.FieldInterceptor
import com.ddecaest.external.DefaultRepositoryModel
import com.ddecaest.internal.jdbc.JdbcQueryBuilder
import com.ddecaest.internal.jdbc.JdbcQueryExecutor
import com.ddecaest.internal.parsing.QueryParser
import java.lang.IllegalArgumentException
import javax.sql.DataSource

internal class DefaultBootstrappedQueryService(
    repositoryModel: DefaultRepositoryModel,
    dataSource: DataSource,
    private val fieldInterceptors: List<FieldInterceptor<Any>>
) : BootstrappedQueryService {

    private val queryBuilder = JdbcQueryBuilder(fieldInterceptors)
    private val queryExecutor = JdbcQueryExecutor(dataSource)
    private val queryParser = QueryParser(repositoryModel)

    init {
        RepositoryModelValidator.errorThrowingValidate(repositoryModel)
        validateFieldInterceptors(repositoryModel)
    }

    private fun validateFieldInterceptors(repositoryModel: DefaultRepositoryModel) {
        fieldInterceptors.forEach {
            if (repositoryModel.getField(it.entityName, it.fieldName) == null) {
                throw IllegalArgumentException("A field interceptor has been defined for field ${it.fieldName} for entity ${it.entityName}, but no such field exists for that entity!")
            }
        }
    }

    override fun executeQuery(rawQuery: String): Any {
        val parsedQuery = queryParser.parse(rawQuery)
        val jdbcQuery = queryBuilder.build(parsedQuery)
        return queryExecutor.execute(jdbcQuery)
    }
}