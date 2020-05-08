package com.ddecaest.internal

import com.ddecaest.external.BootstrappedQueryService
import com.ddecaest.external.FieldInterceptor
import com.ddecaest.external.RepositoryModel
import java.lang.IllegalArgumentException
import javax.sql.DataSource

internal class DefaultBootstrappedQueryService(
    repositoryModel: RepositoryModel,
    dataSource: DataSource,
    private val fieldInterceptors: List<FieldInterceptor<Any>>
) : BootstrappedQueryService {

    private val queryBuilder = JdbcQueryBuilder(repositoryModel)
    private val queryExecutor = JdbcQueryExecutor(dataSource)

    init {
        fieldInterceptors.forEach {
            if (repositoryModel.getField(it.entityName, it.fieldName) == null) {
                throw IllegalArgumentException("A field interceptor has been defined for field ${it.fieldName} for entity ${it.entityName}, but no such field exists for that entity!")
            }
        }
    }

    override fun executeQuery(rawQuery: String): Any {
        val parsedQuery = QueryParser.parse(rawQuery)
        val jdbcQuery = queryBuilder.build(parsedQuery, fieldInterceptors)
        return queryExecutor.execute(jdbcQuery)
    }
}