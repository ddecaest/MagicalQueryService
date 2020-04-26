package com.ddecaest.external

import com.ddecaest.internal.DefaultBootstrappedQueryService
import javax.sql.DataSource

interface BootstrappedQueryService {

    fun executeQuery(rawQuery: String): Any
}

object DefaultBootstrappedQueryServiceFactory {

    fun build(repositoryModel: RepositoryModel, dataSource: DataSource): BootstrappedQueryService {
        return DefaultBootstrappedQueryService(repositoryModel, dataSource)
    }
}