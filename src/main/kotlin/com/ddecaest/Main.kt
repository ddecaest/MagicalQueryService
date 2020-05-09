package com.ddecaest

import com.ddecaest.external.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource


fun main() {
    val dataSource = instantiateDemoDb()
    val demoRepositoryModel = instantiateDemoRepositoryModel()
    val demoInterceptor = FieldInterceptor<Any>("Person", "Username") { "YOU ARE NOT ALLOWED TO SEE THIS" }

    // TODO: Entity/field names could not be case sensitive?
    // TODO: actually parse where clause to be safe from delete from/insert intos
    // => PROFIT

    val factory = DefaultBootstrappedQueryServiceFactory.build(demoRepositoryModel, dataSource, listOf(demoInterceptor))
    val rawQuery = "SElECT Person.Username, Person.Career.Name, Person.Id aS PersonId, Person.Career.Id AS CareerId WHERE Person.Hobby.Name LIKE '%Recreational%'"
    val result = factory.executeQuery(rawQuery)
    println("$rawQuery -> $result")
}

private fun instantiateDemoDb(): DataSource {
    val dataSource = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
    val jdbcTemplate = JdbcTemplate(dataSource)

    val populateDbSql = """
        CREATE TABLE USER (
            Id INT AUTO_INCREMENT PRIMARY KEY,
            UserName VARCHAR(50) NOT NULL,
            FirstName VARCHAR(50) NOT NULL,
            LastName VARCHAR(50) NOT NULL,
            Age INT NOT NULL
        );
        CREATE TABLE JOB (
            Id INT AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(50) NOT NULL,
            UserId INT NOT NULL
        );
        CREATE TABLE HOBBY (
            Id INT AUTO_INCREMENT PRIMARY KEY,
            Name VARCHAR(50) NOT NULL,
            UserId INT NOT NULL
        );
        
        INSERT INTO USER(UserName, FirstName, LastName, Age) VALUES('1337Guy', 'John', 'Doe', 25);
        INSERT INTO JOB(Name, UserId) VALUES('Financial Advisor', 1);
        INSERT INTO JOB(Name, UserId) VALUES('Crocodile Hunter', 1);
        INSERT INTO HOBBY(Name, UserId) VALUES('Origami Folder', 1);
        
        INSERT INTO USER(UserName, FirstName, LastName, Age) VALUES('1337Guy', 'Felicity', 'Felicious', 35);
        INSERT INTO JOB(Name, UserId) VALUES('Basket Painter', 2);
        INSERT INTO HOBBY(Name, UserId) VALUES('Recreational Archer', 2);
    """
    jdbcTemplate.update(populateDbSql)

    return dataSource
}

private fun instantiateDemoRepositoryModel(): RepositoryModel {
    val userFields = listOf(
        Field("Id", "Id", FieldType.LONG),
        Field("Username", "UserName", FieldType.STRING),
        Field("Firstname", "FirstName", FieldType.STRING),
        Field("Age", "Age", FieldType.LONG)
    )
    val userEntity = Entity("Person", "User", userFields)

    val jobFields = listOf(
        Field("Id", "Id", FieldType.LONG),
        Field("Name", "Name", FieldType.STRING),
        Field("User", "UserId", FieldType.LONG)
    )
    val jobEntity = Entity("Career", "Job", jobFields)

    val hobbyFields = listOf(
        Field("Id", "Id", FieldType.LONG),
        Field("Name", "Name", FieldType.STRING),
        Field("User", "UserId", FieldType.LONG)
    )
    val hobbyEntity = Entity("Hobby", "Hobby", hobbyFields)

    val joinBetweenUserAndJob = Join("Person", "Career", "Id", "UserId")
    val joinBetweenUserAndHobby = Join("Person", "Hobby", "Id", "UserId")

    return RepositoryModel(listOf(userEntity, jobEntity, hobbyEntity), listOf(joinBetweenUserAndJob, joinBetweenUserAndHobby))
}