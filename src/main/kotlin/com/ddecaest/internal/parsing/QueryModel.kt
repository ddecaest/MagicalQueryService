package com.ddecaest.internal.parsing

import com.ddecaest.external.FieldType
import com.ddecaest.external.RepositoryModel
import com.ddecaest.internal.parsing.SelectClauseParser.FieldSelected

internal class QueryModel private constructor(private val repositoryModel: RepositoryModel) {

    class ColumnSelected(
        val fieldName: String,
        val columnName: String,
        val columnAlias: String,
        val entityName: String,
        val entityAlias: String,
        val type: FieldType
    )

    class EntityNode(
        val entityName: String,
        val tableName: String,
        val aliasName: String,
        val entitiesJoined: MutableList<JoinedEntity>
    ) {
        class JoinedEntity(
            val joined: EntityNode,
            val originColumn: String,
            val joinedColumn: String
        )
    }

    companion object {

        fun build(
            repositoryModel: RepositoryModel,
            fieldSelection: List<FieldSelected>,
            whereClauseParsed: WhereClauseParser.WhereClauseParsed
        ): QueryModel {
            val queryModel = QueryModel(repositoryModel)
            fieldSelection.forEach(queryModel::addToQueryModel)
            queryModel.addWhereClause(whereClauseParsed)
            return queryModel
        }
    }

    private val uniqueAliasGenerator = UniqueAliasGenerator()

    val selection = mutableListOf<ColumnSelected>()
    var entityTree: EntityNode? = null
    var whereCondition: String = ""

    fun addToQueryModel(fieldSelected: FieldSelected) {
        val correspondingNode = nodeCreatingGetNode(fieldSelected.entityChain)

        val field = repositoryModel.errorThrowingGetField(fieldSelected.fieldName, correspondingNode.entityName)
        selection.add(
            ColumnSelected(
                fieldSelected.fieldName,
                field.columnName,
                fieldSelected.alias ?: fieldSelected.fieldName,
                correspondingNode.entityName,
                correspondingNode.aliasName,
                field.type
            )
        )
    }

    // This does a bit much: it populates the entity tree so joins can be made if necessary and returns the resulting entity
    private fun nodeCreatingGetNode(entityChain: List<String>): EntityNode {
        if (entityTree == null) {
            val entity = repositoryModel.errorThrowingGetEntity(entityChain[0])
            entityTree = EntityNode(
                entity.name,
                entity.tableName,
                uniqueAliasGenerator.generateAlias(entity.name),
                mutableListOf()
            )
        }

        var currentNode = entityTree!!
        var entityChainWithoutCurrentNode = entityChain.subList(1, entityChain.size)

        while (entityChainWithoutCurrentNode.isNotEmpty()) {
            val nextEntityInChain = entityChainWithoutCurrentNode[0]

            val existingNode = currentNode.entitiesJoined.find { it.joined.entityName == nextEntityInChain }
            currentNode = if (existingNode != null) {
                existingNode.joined
            } else {
                val entity = repositoryModel.errorThrowingGetEntity(nextEntityInChain)
                val aliasForEntity = uniqueAliasGenerator.generateAlias(entity.name)
                val childEntity = EntityNode(entity.name, entity.tableName, aliasForEntity, mutableListOf())

                val modelJoin = repositoryModel.errorThrowingGetJoin(currentNode.entityName, nextEntityInChain)
                val queryModelJoin =
                    EntityNode.JoinedEntity(childEntity, modelJoin.entityAJoinColumn, modelJoin.entityBJoinColumn)
                currentNode.entitiesJoined.add(queryModelJoin)

                queryModelJoin.joined
            }

            entityChainWithoutCurrentNode = entityChainWithoutCurrentNode.subList(1, entityChainWithoutCurrentNode.size)
        }

        return currentNode
    }

    fun addWhereClause(whereClauseParsed: WhereClauseParser.WhereClauseParsed) {
        var whereConditionReplaced = whereClauseParsed.rawClause

        whereClauseParsed.fieldsUsed.forEach {
            val correspondingNode = nodeCreatingGetNode(it.entityChain)
            val field = repositoryModel.errorThrowingGetField(it.fieldName, correspondingNode.entityName)

            val originalFieldSelector = "${it.entityChain.joinToString(separator = ".")}.${it.fieldName}"
            val replacementFieldSelector = "${correspondingNode.aliasName}.${field.columnName}"

            whereConditionReplaced = whereConditionReplaced.replace(originalFieldSelector, replacementFieldSelector)
        }

        this.whereCondition = whereConditionReplaced
    }
}

private class UniqueAliasGenerator {

    private val aliasMap = mutableMapOf<String, Int>()

    fun generateAlias(name: String): String {
        val count = aliasMap.getOrPut(name, { 0 })
        return "$name$count"
    }
}