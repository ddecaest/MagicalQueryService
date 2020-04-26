package com.ddecaest

import com.ddecaest.external.DefaultBootstrappedQueryServiceFactory
import com.ddecaest.external.Entity
import com.ddecaest.external.RepositoryModel
import org.h2.Driver
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import javax.sql.DataSource

fun main() {
    val demoRepositoryModel = RepositoryModel(
        listOf(
            Entity(
                "User", "User",
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

    val dataSource: DataSource = SimpleDriverDataSource(Driver(), "jdbc:h2:mem:test", "SA", "pass")
    instantiateDemoDb(dataSource)

    val factory = DefaultBootstrappedQueryServiceFactory.build(demoRepositoryModel, dataSource)

    val result = factory.executeQuery("SELECT User.UserName")

    println("result")
}

fun instantiateDemoDb(dataSource: DataSource) {
    val jdbcTemplate = JdbcTemplate(dataSource)
    val sql = """
        CREATE TABLE User (
            UserName INT NOT NULL,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Age INT NOT NULL,
            BirthDate TIMESTAMP NOT NULL
            )
    """
    jdbcTemplate.update(sql)
}