package com.ddecaest.internal

import com.ddecaest.external.BootstrappedQueryService
import com.ddecaest.external.RepositoryModel
import javax.sql.DataSource

internal class DefaultBootstrappedQueryService(
    repositoryModel: RepositoryModel,
    dataSource: DataSource
) : BootstrappedQueryService {

    private val queryBuilder = JdbcQueryBuilder(repositoryModel)
    private val queryExecutor = JdbcQueryExecutor(dataSource)

    override fun executeQuery(rawQuery: String): Any {
        val parsedQuery = QueryParser.parse(rawQuery)
        val jdbcQuery = queryBuilder.build(parsedQuery)
        return queryExecutor.execute(jdbcQuery)
    }
}