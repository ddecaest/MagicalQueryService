package com.ddecaest.internal.repositorymodel

import com.ddecaest.external.Entity
import com.ddecaest.external.Field
import com.ddecaest.external.Join
import com.ddecaest.external.RepositoryModel

internal class DefaultRepositoryModel(
    private val entities: List<Entity>,
    private val joins: List<Join>
) : RepositoryModel {

    override fun getEntities(): List<Entity> {
        return entities;
    }

    override fun getJoins(): List<Join> {
        return joins;
    }

    override fun getField(entityName: String, fieldName: String): Field? {
        return getEntity(entityName)?.fields?.find { it -> it.name.toUpperCase() == fieldName.toUpperCase() }
    }

    override fun getEntity(entityName: String): Entity? {
        return entities.find { it.name.toUpperCase() == entityName.toUpperCase() }
    }

    override fun getJoin(entityA: String, entityB: String): Join? {
        return joins.find {
            (it.entityA.toUpperCase() == entityA.toUpperCase() && it.entityB.toUpperCase() == entityB.toUpperCase()) ||
                    (it.entityA.toUpperCase() == entityB.toUpperCase() && it.entityB.toUpperCase() == entityA.toUpperCase())
        }
    }
}
