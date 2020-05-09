package com.ddecaest.external

class RepositoryModel(private val entities: List<Entity>, private val joins: List<Join>) {

    fun getField(entityName: String, fieldName: String): Field? {
        return getEntity(entityName)?.fields?.find { it -> it.name == fieldName }
    }

    private fun getEntity(entityName: String): Entity? {
        return entities.find { it.name == entityName }
    }

    private fun getJoin(entityA: String, entityB: String): Join? {
        return joins.find {
            (it.entityA == entityA && it.entityB == entityB) || (it.entityA == entityB && it.entityB == entityA)
        }
    }

    fun errorThrowingGetEntity(entityName: String): Entity {
        return getEntity(entityName)
            ?: throw IllegalArgumentException("There is no table mapped to the unknown entity $entityName")
    }

    fun errorThrowingGetField(fieldName: String, entityName: String): Field {
        return (getField(entityName, fieldName)
            ?: throw IllegalArgumentException("There is no field $fieldName mapped to the entity $entityName"))
    }

    fun errorThrowingGetJoin(entityNameA: String, entityNameB: String): Join {
        return (getJoin(entityNameA, entityNameB)
            ?: throw IllegalArgumentException("There is no mapped join between $entityNameA and entity $entityNameB"))
    }
}

data class Entity(val name: String, val tableName: String, val fields: Collection<Field>)

data class Field(val name: String, val columnName: String, val type: FieldType)

data class Join(val entityA: String, val entityB: String, val entityAJoinColumn: String, val entityBJoinColumn: String)

enum class FieldType {
    STRING,
    LONG
}