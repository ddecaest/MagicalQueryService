package com.ddecaest.internal

import com.ddecaest.external.*

internal class QueryModel(private val repositoryModel: RepositoryModel) {

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
        var entitiesJoined: MutableList<JoinedEntity>
    )

    class JoinedEntity(
        val joined: EntityNode,
        val originColumn: String,
        val joinedColumn: String
    )

    private val uniqueAliasGenerator = UniqueAliasGenerator()
    val selection = mutableListOf<ColumnSelected>()
    var entityTree: EntityNode? = null

    fun addToQueryModel(fieldSelected: QueryParser.ParsedQuery.FieldSelected) {
        if (entityTree == null) {
            entityTree = createNode(fieldSelected.entityChain[0])
        }
        visitNode(fieldSelected.fieldName, fieldSelected.entityChain, entityTree!!, fieldSelected.alias)
    }

    private fun visitNode(
        fieldName: String,
        entityChain: List<String>,
        currentNode: EntityNode,
        fieldAlias: String?
    ) {
        val entityChainWithoutCurrentNode = entityChain.subList(1, entityChain.size)

        if (entityChainWithoutCurrentNode.isEmpty()) {
            // We have reached the entity in question
            selection.add(createColumn(fieldName, currentNode, fieldAlias))
            return
        }

        val nextEntityInChain = entityChainWithoutCurrentNode[0]

        val alreadyCreatedJoin = currentNode.entitiesJoined.find { it.joined.entityName == nextEntityInChain }
        if (alreadyCreatedJoin != null) {
            visitNode(fieldName, entityChainWithoutCurrentNode, alreadyCreatedJoin.joined, fieldAlias)
        } else {
            val childEntity = createNode(nextEntityInChain)
            val modelJoin = getJoin(currentNode.entityName, nextEntityInChain)
            val queryModelJoin = JoinedEntity(childEntity, modelJoin.entityAJoinColumn, modelJoin.entityBJoinColumn)

            currentNode.entitiesJoined.add(queryModelJoin)

            visitNode(fieldName, entityChainWithoutCurrentNode, childEntity, fieldAlias)
        }
    }

    private fun createColumn(
        fieldName: String,
        currentNode: EntityNode,
        fieldAlias: String?
    ): ColumnSelected {
        val field = getField(fieldName, currentNode.entityName)
        val columnAlias = fieldAlias ?: fieldName
        return ColumnSelected(fieldName, field.columnName, columnAlias, currentNode.entityName, currentNode.aliasName, field.type)
    }

    private fun createNode(entityName: String): EntityNode {
        val entity = getEntity(entityName)
        return EntityNode(
            entity.name,
            entity.tableName,
            uniqueAliasGenerator.generateAlias(entity.name),
            mutableListOf()
        )
    }

    private fun getEntity(entityName: String): Entity {
        return repositoryModel.getEntity(entityName)
            ?: throw IllegalArgumentException("There is no table mapped to the unknown entity $entityName")
    }

    private fun getField(fieldName: String, entityName: String): Field {
        return (repositoryModel.getField(entityName, fieldName)
            ?: throw IllegalArgumentException("There is no field $fieldName mapped to the entity $entityName"))
    }

    private fun getJoin(entityNameA: String, entityNameB: String): Join {
        return (repositoryModel.getJoinColumns(entityNameA, entityNameB)
            ?: throw IllegalArgumentException("There is no mapped join between $entityNameA and entity $entityNameB"))
    }


    private class UniqueAliasGenerator {

        private val aliasMap = mutableMapOf<String, Int>()

        fun generateAlias(name: String): String {
            val count = aliasMap.getOrPut(name, { 0 })
            return "$name$count"
        }
    }
}