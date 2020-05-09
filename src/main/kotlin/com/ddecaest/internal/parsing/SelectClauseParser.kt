package com.ddecaest.internal.parsing

internal object SelectClauseParser {
    class FieldSelected(val entityChain: List<String>, val fieldName: String, val alias: String?)

    fun parse(selectClause: String): List<FieldSelected> {
        val parsedFieldsSelected = selectClause.split(",").mapNotNull(this::parseFieldSelected).toList()
        errorThrowingValidateSelectionNoDuplicates(parsedFieldsSelected)
        return parsedFieldsSelected
    }

    private fun parseFieldSelected(rawFieldSelector: String): FieldSelected? {
        val trimmedSelector = rawFieldSelector.trim()
        if (trimmedSelector.isEmpty()) {
            return null
        }

        val splitOnOptionalAlias = trimmedSelector.split(" AS ")
        if (splitOnOptionalAlias.size > 2) {
            throw IllegalArgumentException("Malformed query : field selector $rawFieldSelector contained ' AS ' more than once!")
        }

        val rawFieldSelectorChain = splitOnOptionalAlias[0].replace(" ", "")
        val rawAlias = if (splitOnOptionalAlias.size > 1) {
            splitOnOptionalAlias[1].replace(" ", "")
        } else {
            null
        }

        val fieldSelector = FieldSelectorParser.parse(rawFieldSelectorChain)
        return FieldSelected(fieldSelector.entityChain, fieldSelector.fieldName, rawAlias)
    }

    private fun errorThrowingValidateSelectionNoDuplicates(selections: List<FieldSelected>) {
        val namesUsed = mutableSetOf<String>()
        selections.forEach {
            val nameUsedForField = it.alias ?: it.fieldName
            if (!namesUsed.add(nameUsedForField)) {
                throw IllegalArgumentException("$nameUsedForField is contained twice in the result! Please use an alias so each field has a unique name!")
            }
        }
    }
}