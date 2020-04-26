package com.ddecaest

import javax.sql.DataSource

interface BootstrappedQueryService {

    fun executeQuery(rawQuery: String): Any
}

internal class DefaultBootstrappedQueryService(repositoryModel: RepositoryModel, dataSource: DataSource) : BootstrappedQueryService {

    private val queryBuilder = JdbcQueryBuilder(repositoryModel)
    private val queryExecutor = JdbcQueryExecutor(dataSource)

    override fun executeQuery(rawQuery: String): Any {
        val query = queryBuilder.build(rawQuery)
        return queryExecutor.execute(query)
    }
}

object DefaultBootstrappedQueryServiceFactory {

    fun build(repositoryModel: RepositoryModel, dataSource: DataSource): BootstrappedQueryService {
        return DefaultBootstrappedQueryService(repositoryModel, dataSource)
    }
}