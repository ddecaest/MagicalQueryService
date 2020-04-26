package com.ddecaest

import javax.sql.DataSource

fun main() {

    val demoRepositoryModel = RepositoryModel(
        listOf(
            Entity("User" ,"User",
                mapOf(
                    "UserName" to "UserName",
                    "FirstName" to "FirstName",
                    "LastName" to "LastName",
                    "Age" to "Age",
                    "BirthDate" to "BirthDate"
                )
            )
        )
    )

//    val dataSource: DataSource  = BasicDataSource()
//    DefaultBootstrappedQueryServiceFactory.build(RepositoryModel(emptyList()), dataSource)

    println("hah")
}