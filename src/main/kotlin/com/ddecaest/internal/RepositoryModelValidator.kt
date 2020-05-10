package com.ddecaest.internal

import com.ddecaest.external.Join
import com.ddecaest.external.DefaultRepositoryModel
import java.lang.IllegalStateException

internal object RepositoryModelValidator {

    fun errorThrowingValidate(repositoryModel: DefaultRepositoryModel) {
        errorThrowingValidateEntityNames(repositoryModel)
        errorThrowingValidateFieldNames(repositoryModel)
        errorThrowingValidateJoins(repositoryModel)
    }

    private fun errorThrowingValidateFieldNames(repositoryModel: DefaultRepositoryModel) {
        repositoryModel.entities.forEach { entity ->
            val existingFieldNamesUpperCase = mutableSetOf<String>()
            entity.fields.forEach {field ->
                val added = existingFieldNamesUpperCase.add(field.name.toUpperCase())
                if (!added) {
                    throw duplicateFieldNameException(entity.name, field.name)
                }
            }
        }
    }

    private fun errorThrowingValidateEntityNames(repositoryModel: DefaultRepositoryModel) {
        val existingSetNamesUpperCase = mutableSetOf<String>()
        repositoryModel.entities.forEach {
            val added = existingSetNamesUpperCase.add(it.name.toUpperCase())
            if (!added) {
                throw duplicateEntityNameException(it.name)
            }
        }
    }

    private fun errorThrowingValidateJoins(repositoryModel: DefaultRepositoryModel) {
        val existingJoins = mutableSetOf<JoinEntities>()
        repositoryModel.joins.forEach {
            val added = existingJoins.add(JoinEntities(it.entityA, it.entityB))
            if (!added) {
                throw duplicateJoinException(it)
            }
        }
        repositoryModel.joins.forEach {
            val added = existingJoins.add(JoinEntities(it.entityB, it.entityA))
            if (!added) {
                throw duplicateJoinException(it)
            }
        }
    }

    private fun duplicateEntityNameException(name: String): Throwable {
        return IllegalStateException("Repository model contains duplicate entity name $name")
    }

    private fun duplicateFieldNameException(entity: String, field: String): Throwable {
        return IllegalStateException("Repository model contains duplicate field name $field for entity $entity")
    }

    private fun duplicateJoinException(it: Join): IllegalStateException {
        return IllegalStateException("Repository model contains duplicate join (${it.entityA},${it.entityB}")
    }

    private data class JoinEntities(val entity1: String, val entity2: String)
}