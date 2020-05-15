package com.ddecaest.internal.parsing

import com.ddecaest.external.RepositoryModel

internal class QueryParser(private val repositoryModel: RepositoryModel) {

    companion object {
        internal const val selectKeyword = "SELECT "
        internal const val whereKeyword = " WHERE "
        internal const val asKeyword = " AS "

        internal val caseInsensitiveSelect = Regex("(?i)$selectKeyword")
        internal val caseInsensitiveWhere = Regex("(?i)$whereKeyword")
        internal val caseInsensitiveAs = Regex("(?i)$asKeyword")
    }

    fun parse(rawQuery: String): QueryModel {
        val preprocessedQuery = RawQueryPreprocessor.preprocess(rawQuery)
        val clauses = ClauseSplitter.split(preprocessedQuery)

        val selectClauseParsed = SelectClauseParser.parse(clauses.selectClause)
        val whereClauseParsed = WhereClauseParser.parse(clauses.whereClause)

        return QueryModel.build(repositoryModel, selectClauseParsed, whereClauseParsed)
    }
}

internal object WhereClauseParser {
    class WhereClauseParsed(val rawClause: String, val fieldsUsed: List<FieldInConditions>)
    class FieldInConditions(val entityChain: List<String>, val fieldName: String)

    private val fieldSelectorRegex = Regex("([A-Z|a-z|0-9]+\\.)+[A-Z|a-z|0-9]+")

    fun parse(rawWhereClause: String): WhereClauseParsed {
        val fieldSelectors = fieldSelectorRegex.findAll(rawWhereClause).map { FieldSelectorParser.parse(it.value) }.toList()
        val asFieldsInCondition = fieldSelectors.map { FieldInConditions(it.entityChain, it.fieldName) }
        return WhereClauseParsed(rawWhereClause, asFieldsInCondition)
    }
}