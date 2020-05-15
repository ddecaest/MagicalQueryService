package com.ddecaest.internal.repositorymodel

import com.ddecaest.external.Join
import com.ddecaest.external.RepositoryModel

internal object RepositoryModelValidator {

    fun errorThrowingValidate(repositoryModel: RepositoryModel) {
        errorThrowingValidateEntityNames(repositoryModel)
        errorThrowingValidateFieldNames(repositoryModel)
        errorThrowingValidateJoins(repositoryModel)
    }

    private fun errorThrowingValidateFieldNames(repositoryModel: RepositoryModel) {
        repositoryModel.getEntities().forEach { entity ->
            val existingFieldNamesUpperCase = mutableSetOf<String>()
            entity.fields.forEach { field ->
                val added = existingFieldNamesUpperCase.add(field.name.toUpperCase())
                if (!added) {
                    throw duplicateFieldNameException(entity.name, field.name)
                }
            }
        }
    }

    private fun errorThrowingValidateEntityNames(repositoryModel: RepositoryModel) {
        val existingSetNamesUpperCase = mutableSetOf<String>()
        repositoryModel.getEntities().forEach {
            val added = existingSetNamesUpperCase.add(it.name.toUpperCase())
            if (!added) {
                throw duplicateEntityNameException(it.name)
            }
        }
    }

    private fun errorThrowingValidateJoins(repositoryModel: RepositoryModel) {
        val existingJoins = mutableSetOf<JoinEntities>()
        repositoryModel.getJoins().forEach {
            val added = existingJoins.add(JoinEntities(it.entityA, it.entityB))
            if (!added) {
                throw duplicateJoinException(it)
            }
        }
        repositoryModel.getJoins().forEach {
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