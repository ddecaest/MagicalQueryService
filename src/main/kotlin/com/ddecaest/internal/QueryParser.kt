package com.ddecaest.internal

import com.ddecaest.internal.QueryParser.ParsedQuery.FieldSelected

object QueryParser {

    fun parse(rawQuery: String): ParsedQuery {
        val clauses = splitInSelectAndWhereClause(rawQuery)
        val selectClause = clauses.selectClause
        val whereClause = clauses.whereClause

        val entitiesSelected = parseSelectClause(selectClause)
        // TODO : parse where selected

        return ParsedQuery(entitiesSelected)
    }

    private class SplitQuery(val selectClause: String, val whereClause: String)

    private fun splitInSelectAndWhereClause(rawQuery: String): SplitQuery {

        if (!rawQuery.startsWith("SELECT ")) {
            throw IllegalArgumentException("Malformed query : must start with 'SELECT '")
        }
        val splitOnWhere = rawQuery.split("WHERE ")
        if (splitOnWhere.size > 2) {
            throw IllegalArgumentException("Malformed query : contained 'WHERE ' more than once")
        }
        val selectClause = splitOnWhere[0].substring(7)
        val whereClause = if (splitOnWhere.size == 2) {
            splitOnWhere[1]
        } else {
            ""
        }

        return SplitQuery(selectClause, whereClause)
    }

    private fun parseSelectClause(selectClause: String): List<FieldSelected> {
        val fieldSelectorsParsed = mutableListOf<FieldSelected>()

        val fieldSelectors = selectClause.replace(" ", "").split(",")
        for (fieldSelector in fieldSelectors) {
            if (fieldSelector.isEmpty()) {
                continue
            }
            val splitOnDot = fieldSelector.split(".")
            if (splitOnDot.size == 1) {
                throw IllegalArgumentException("Malformed query : field selector $fieldSelector must be formed like [ENTITY.]+FIELD)")
            }

            val entities = splitOnDot.subList(0, splitOnDot.size - 1)
            val field = splitOnDot[splitOnDot.size - 1]

            fieldSelectorsParsed.add(FieldSelected(entities, field))
        }
        return fieldSelectorsParsed
    }


    class ParsedQuery(val fieldsSelected: List<FieldSelected>) {

        class FieldSelected(
            val entityChain: List<String>,
            val fieldName: String
        )
    }
}
