package com.ddecaest.internal.parsing

internal object FieldSelectorParser {
    class FieldSelector(val entityChain: List<String>, val fieldName: String)

    fun parse(rawFieldSelectorChain: String): FieldSelector {
        val fieldSelectorChain = rawFieldSelectorChain.split(".")
        if (fieldSelectorChain.size == 1) {
            throw IllegalArgumentException("Malformed query : field selector $rawFieldSelectorChain must be formed like [ENTITY.]+FIELD)")
        }

        val entities = fieldSelectorChain.subList(0, fieldSelectorChain.size - 1)
        val field = fieldSelectorChain[fieldSelectorChain.size - 1]
        return FieldSelector(entities, field)
    }
}