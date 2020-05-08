package com.ddecaest.internal

import com.ddecaest.internal.QueryParser.ParsedQuery.FieldSelected

object QueryParser {

    private const val selectKeyword = "SELECT "
    private const val whereKeyword = " WHERE "
    private const val asKeyword = " AS "

    private val caseInsensitiveSelect = Regex("(?i)$selectKeyword")
    private val caseInsensitiveWhere = Regex("(?i)$whereKeyword")
    private val caseInsensitiveAs = Regex("(?i)$asKeyword")
    /**
     * Parses the raw query into a list of selectors.
     * Does not actually check whether the query is executable, only that the syntax is valid.
     */
    fun parse(rawQuery: String): ParsedQuery {
        val preprocessedQuery = ensureKeyWordsAreUpperCase(rawQuery)

        val clauses = splitInSelectAndWhereClause(preprocessedQuery)
        val whereClause = clauses.whereClause

        val entitiesSelected = SelectClauseParser.parse(clauses.selectClause)
        // TODO : parse where selected

        val parsedQuery = ParsedQuery(entitiesSelected)
        errorThrowingValidateSelectionNoDuplicates(parsedQuery)
        return parsedQuery
    }

    private fun ensureKeyWordsAreUpperCase(rawQuery: String): String {
        return rawQuery.replace(caseInsensitiveSelect, selectKeyword)
            .replace(caseInsensitiveWhere, whereKeyword)
            .replace(caseInsensitiveAs, asKeyword)
    }

    private fun splitInSelectAndWhereClause(rawQuery: String): SplitQuery {
        if (!rawQuery.startsWith(selectKeyword)) {
            throw IllegalArgumentException("Malformed query : must start with '$selectKeyword'")
        }
        val withoutSelect = rawQuery.substring(selectKeyword.lastIndex)

        val splitOnWhere = withoutSelect.split(whereKeyword)
        if (splitOnWhere.size > 2) {
            throw IllegalArgumentException("Malformed query : contained '$whereKeyword' more than once")
        }

        val selectClauseWithoutKeyWord = splitOnWhere[0]
        val whereClauseWithoutKeyWord = if (splitOnWhere.size == 2) {
            splitOnWhere[1]
        } else {
            ""
        }

        return SplitQuery(selectClauseWithoutKeyWord, whereClauseWithoutKeyWord)
    }

    private fun errorThrowingValidateSelectionNoDuplicates(query: ParsedQuery) {
        val namesUsed = mutableSetOf<String>()
        query.fieldsSelected.forEach {
            val nameUsedForField = it.alias ?: it.fieldName
            if (!namesUsed.add(nameUsedForField)) {
                throw IllegalArgumentException("$nameUsedForField is contained twice in the result! Please use an alias so each field has a unique name!")
            }
        }
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