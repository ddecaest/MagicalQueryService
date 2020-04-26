package com.ddecaest

class RepositoryModel(val entities: List<Entity>)

class Entity(val name: String, val tableName: TableName, val fields: Map<String, ColumnName>)

typealias TableName = String
typealias ColumnName = String