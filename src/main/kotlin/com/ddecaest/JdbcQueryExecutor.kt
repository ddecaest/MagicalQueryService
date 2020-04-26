package com.ddecaest

import javax.sql.DataSource

class JdbcQueryExecutor(private val dataSource: DataSource) {

    fun execute(query: JdbcQueryBuilder.JdbcQuery): Any {
        return listOf<String>()
    }
}