package com.ddecaest.external

import com.ddecaest.internal.DefaultBootstrappedQueryService
import javax.sql.DataSource

interface BootstrappedQueryService {

    fun executeQuery(rawQuery: String): Any
}

object DefaultBootstrappedQueryServiceFactory {

    fun build(
        repositoryModel: DefaultRepositoryModel,
        dataSource: DataSource,
        fieldInterceptors: List<FieldInterceptor<Any>> = listOf()
    ): BootstrappedQueryService {
        return DefaultBootstrappedQueryService(repositoryModel, dataSource, fieldInterceptors)
    }
}