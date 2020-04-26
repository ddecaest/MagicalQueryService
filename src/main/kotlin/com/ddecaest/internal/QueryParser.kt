package com.ddecaest.internal

object QueryParser {

    fun parse(rawQuery: String): Query {
        val removeWhiteLines = rawQuery.replace(" ", "")
        if(!removeWhiteLines.startsWith("SELECT")) {
            throw IllegalArgumentException("Malformed query : must start with SELECT")
        }

        val selectedFieldsParsed = mutableListOf<SelectedField>()

        val queryWithoutSelect = removeWhiteLines.substring(6)
        val selectedFields = queryWithoutSelect.split(",")
        for(selectedField in selectedFields) {
            if(selectedField.isEmpty()) {
                continue
            }
            val amountOfDotsInSelectedField = selectedField.count { c: Char -> c == '.' }
            if(amountOfDotsInSelectedField != 1) {
                throw IllegalArgumentException("Malformed field selector $selectedField : contains more than one '.'.")
            }
            val splitOnDot = selectedField.split(".")

            selectedFieldsParsed.add(SelectedField(splitOnDot[0], splitOnDot[1]))
        }

        return Query(selectedFieldsParsed)
    }
}

class Query(val fields: List<SelectedField>)

class SelectedField(val entityName: String, val fieldName: String)