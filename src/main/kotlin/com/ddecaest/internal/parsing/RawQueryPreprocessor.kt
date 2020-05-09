package com.ddecaest.internal.parsing

internal object RawQueryPreprocessor {

    fun preprocess(rawQuery: String): String {
        return rawQuery
            .replace(QueryParser.caseInsensitiveSelect, QueryParser.selectKeyword)
            .replace(QueryParser.caseInsensitiveWhere, QueryParser.whereKeyword)
            .replace(QueryParser.caseInsensitiveAs, QueryParser.asKeyword)
    }
}