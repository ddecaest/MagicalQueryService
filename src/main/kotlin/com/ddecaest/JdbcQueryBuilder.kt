package com.ddecaest

class JdbcQueryBuilder(private val repositoryModel: RepositoryModel) {

    fun build(query: String): JdbcQuery {
        var a = "bd"

        return JdbcQuery("TODO", emptyMap())
    }

    class JdbcQuery(val sql: String, val params: Map<String, Any>)
}