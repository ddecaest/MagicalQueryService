package com.ddecaest.external

import com.ddecaest.internal.repositorymodel.DefaultRepositoryModel

interface RepositoryModel {

    fun getField(entityName: String, fieldName: String): Field?
    fun getJoin(entityA: String, entityB: String): Join?
    fun getEntity(entityName: String): Entity?

    fun getEntities(): List<Entity>
    fun getJoins(): List<Join>

    fun errorThrowingGetField(fieldName: String, entityName: String): Field {
        return (getField(entityName, fieldName)
            ?: throw IllegalArgumentException("There is no field $fieldName mapped to the entity $entityName"))
    }

    fun errorThrowingGetJoin(entityNameA: String, entityNameB: String): Join {
        return (getJoin(entityNameA, entityNameB)
            ?: throw IllegalArgumentException("There is no mapped join between $entityNameA and entity $entityNameB"))
    }

    fun errorThrowingGetEntity(entityName: String): Entity {
        return getEntity(entityName)
            ?: throw IllegalArgumentException("There is no table mapped to the unknown entity $entityName")
    }
}

data class Entity(val name: String, val tableName: String, val fields: Collection<Field>)

data class Field(val name: String, val columnName: String, val type: FieldType)

data class Join(val entityA: String, val entityB: String, val entityAJoinColumn: String, val entityBJoinColumn: String)

enum class FieldType {
    STRING,
    LONG
}

object RepositoryModelFactory {
    fun build(entities: List<Entity>, joins: List<Join>): RepositoryModel {
        return DefaultRepositoryModel(entities, joins)
    }
}
