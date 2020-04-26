package com.ddecaest.internal

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

internal class JdbcQueryExecutor(dataSource: DataSource) {

    private val namedParameterJdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    fun execute(query: JdbcQueryBuilder.JdbcQuery): Any {
        return namedParameterJdbcTemplate.query(query.sql, query.params, query.rowMapper)
    }
}