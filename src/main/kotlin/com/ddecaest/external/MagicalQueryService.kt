package com.ddecaest.external

import com.ddecaest.internal.DefaultMagicalQueryService
import javax.sql.DataSource

interface MagicalQueryService {

    fun executeQuery(rawQuery: String): Any
}

object MagicalQueryServiceFactory {

    fun build(
        repositoryModel: RepositoryModel,
        dataSource: DataSource,
        fieldInterceptors: List<FieldInterceptor<Any>> = listOf()
    ): MagicalQueryService {
        return DefaultMagicalQueryService(repositoryModel, dataSource, fieldInterceptors)
    }
}