package com.ddecaest.internal.parsing

internal object ClauseSplitter {
    class SplitQuery(val selectClause: String, val whereClause: String)

    fun split(rawQuery: String): SplitQuery {
        if (!rawQuery.startsWith(QueryParser.selectKeyword)) {
            throw IllegalArgumentException("Malformed query : must start with '${QueryParser.selectKeyword}'")
        }
        val withoutSelect = rawQuery.substring(QueryParser.selectKeyword.lastIndex)

        val splitOnWhere = withoutSelect.split(QueryParser.whereKeyword)
        if (splitOnWhere.size > 2) {
            throw IllegalArgumentException("Malformed query : contained '${QueryParser.whereKeyword}' more than once")
        }

        val selectClauseWithoutKeyWord = splitOnWhere[0]
        val whereClauseWithoutKeyWord = when (splitOnWhere.size) {
            1 -> ""
            else -> splitOnWhere[1]
        }

        return SplitQuery(selectClauseWithoutKeyWord, whereClauseWithoutKeyWord)
    }
}