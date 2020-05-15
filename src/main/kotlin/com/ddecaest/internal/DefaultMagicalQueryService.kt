package com.ddecaest.internal

import com.ddecaest.external.MagicalQueryService
import com.ddecaest.external.FieldInterceptor
import com.ddecaest.external.RepositoryModel
import com.ddecaest.internal.jdbc.JdbcQueryBuilder
import com.ddecaest.internal.jdbc.JdbcQueryExecutor
import com.ddecaest.internal.parsing.QueryParser
import com.ddecaest.internal.repositorymodel.RepositoryModelValidator
import java.lang.IllegalArgumentException
import javax.sql.DataSource

internal class DefaultMagicalQueryService(
    repositoryModel: RepositoryModel,
    dataSource: DataSource,
    private val fieldInterceptors: List<FieldInterceptor<Any>>
) : MagicalQueryService {

    private val queryBuilder = JdbcQueryBuilder(fieldInterceptors)
    private val queryExecutor = JdbcQueryExecutor(dataSource)
    private val queryParser = QueryParser(repositoryModel)

    init {
        RepositoryModelValidator.errorThrowingValidate(repositoryModel)
        validateFieldInterceptors(repositoryModel)
    }

    private fun validateFieldInterceptors(repositoryModel: RepositoryModel) {
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