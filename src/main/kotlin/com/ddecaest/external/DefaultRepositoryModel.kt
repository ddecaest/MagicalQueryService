package com.ddecaest.external

class DefaultRepositoryModel(val entities: List<Entity>, val joins: List<Join>) {

    fun getField(entityName: String, fieldName: String): Field? {
        return getEntity(entityName)?.fields?.find { it -> it.name.toUpperCase() == fieldName.toUpperCase() }
    }

    private fun getEntity(entityName: String): Entity? {
        return entities.find { it.name.toUpperCase() == entityName.toUpperCase() }
    }

    private fun getJoin(entityA: String, entityB: String): Join? {
        return joins.find {
            (it.entityA.toUpperCase() == entityA.toUpperCase() && it.entityB.toUpperCase() == entityB.toUpperCase()) ||
                    (it.entityA.toUpperCase() == entityB.toUpperCase() && it.entityB.toUpperCase() == entityA.toUpperCase())
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