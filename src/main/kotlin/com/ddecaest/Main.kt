package com.ddecaest

import com.ddecaest.external.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource


fun main() {
    val dataSource = instantiateDemoDb()
    val demoRepositoryModel = instantiateDemoRepositoryModel()

    val factory = DefaultBootstrappedQueryServiceFactory.build(demoRepositoryModel, dataSource)
    val result = factory.executeQuery("SELECT User.Username")
    println(result)
}

private fun instantiateDemoDb(): DataSource {
    val dataSource = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
    val jdbcTemplate = JdbcTemplate(dataSource)

    val createTableSql = """
        CREATE TABLE USER (
            UserName VARCHAR(50) NOT NULL,
            FirstName VARCHAR(50) NOT NULL,
            Age INT NOT NULL
            )
    """
    jdbcTemplate.update(createTableSql)

    val populateTableSql = """
        INSERT INTO USER(UserName, FirstName, Age) VALUES('Dragonex', 'Bob', 12)
    """
    jdbcTemplate.update(populateTableSql)

    return dataSource
}

private fun instantiateDemoRepositoryModel(): RepositoryModel {
    val demoFields = listOf(
        Field("Username", "UserName", FieldType.STRING),
        Field("Firstname", "FirstName", FieldType.STRING),
        Field("Age", "Age", FieldType.LONG)
    )
    val demoEntity = Entity("User", "User", demoFields)
    return RepositoryModel(listOf(demoEntity))
}