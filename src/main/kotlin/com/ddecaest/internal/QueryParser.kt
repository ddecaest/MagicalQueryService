package com.ddecaest.internal

import com.ddecaest.internal.QueryParser.ParsedQuery.FieldSelected

object QueryParser {

    /**
     * Parses the raw query into a list of selectors.
     * Does not actually check whether the query is executable, only that the syntax is valid.
     */
    fun parse(rawQuery: String): ParsedQuery {
        val clauses = splitInSelectAndWhereClause(rawQuery)
        val whereClause = clauses.whereClause

        val entitiesSelected = SelectClauseParser.parse(clauses.selectClause)
        // TODO : parse where selected

        return ParsedQuery(entitiesSelected)
    }

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


    private class SplitQuery(val selectClause: String, val whereClause: String)

    class ParsedQuery(val fieldsSelected: List<FieldSelected>) {

        class FieldSelected(
            val entityChain: List<String>,
            val fieldName: String,
            val alias: String?
        )
    }
}

private object SelectClauseParser {

    fun parse(selectClause: String): List<FieldSelected> {
        return selectClause.split(",").mapNotNull { parseFieldSelected(it) }.toList()
    }

    private fun parseFieldSelected(fieldSelector: String): FieldSelected? {
        val trimmedSelector = fieldSelector.trim()
        if (trimmedSelector.isEmpty()) {
            return null
        }

        val splitOnOptionalAlias = trimmedSelector.split(" AS ")
        if (splitOnOptionalAlias.size > 2) {
            throw IllegalArgumentException("Malformed query : field selector $fieldSelector contained ' AS ' more than once!")
        }

        val rawFieldSelectorChain = splitOnOptionalAlias[0].replace(" ", "")
        val rawAlias = if (splitOnOptionalAlias.size > 1) {
            splitOnOptionalAlias[1].replace(" ", "")
        } else {
            null
        }

        val (entities, field) = parseFieldSelectorChain(rawFieldSelectorChain)
        return FieldSelected(entities, field, rawAlias)
    }

    private fun parseFieldSelectorChain(rawFieldSelectorChain: String): Pair<List<String>, String> {
        val fieldSelectorChain = rawFieldSelectorChain.split(".")
        if (fieldSelectorChain.size == 1) {
            throw IllegalArgumentException("Malformed query : field selector $rawFieldSelectorChain must be formed like [ENTITY.]+FIELD)")
        }

        val entities = fieldSelectorChain.subList(0, fieldSelectorChain.size - 1)
        val field = fieldSelectorChain[fieldSelectorChain.size - 1]
        return Pair(entities, field)
    }
}