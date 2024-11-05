# jOOQ Spring Integration

**jooq-spring** is a small open-source library that provides integration of [jOOQ](https://www.jooq.org) with
the [Spring Framework](https://spring.io/projects/spring-framework).

## Dependency

Add a dependency to the current version:

```xml

<dependency>
    <groupId>ch.martinelli.oss</groupId>
    <artifactId>jooq-spring</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Components

### JooqDAO

The JooqDAO follows the [DAO pattern](https://en.wikipedia.org/wiki/Data_access_object) and not
the [Repository pattern](https://martinfowler.com/eaaCatalog/repository.html) because the Repository is a pattern from
Domain Driven Design (DDD).

#### Methods

Checkout the code for documentation [JooqDAO](src/main/java/ch/martinelli/oss/jooqspring/JooqDAO.java)

- ```int count()```<br>
  Counts the total number of records in the associated table.
- ```int count(org.jooq.Condition condition)```<br>
  Counts the number of records in the associated table that match the given condition.
- ```int delete(org.jooq.Condition condition)```<br>
  Deletes records from the database that match the given condition.
- ```int delete(R record)```<br>
  Deletes the specified record from the database.
- ```int deleteById(ID id)```<br>
  Deletes a record from the database identified by its primary key.
- ```List<R> findAll(int offset, int limit, List<org.jooq.OrderField<?>> orderBy)```<br>
  Retrieves a list of records from the database with pagination and sorting.
- ```List<R> findAll(org.jooq.Condition condition, int offset, int limit, List<org.jooq.OrderField<?>> orderBy)```<br>
  Retrieves a list of records from the database with filtering, pagination, and sorting.
- ```Optional<R> findById(ID id)```<br>
  Finds a record by its primary key.
- ```int merge(R record)```<br>
  Merges (INSERT … ON DUPLICATE KEY UPDATE) the given record into the database.
- ```int save(R record)```<br>
  Saves (INSERT or UPDATE) the given record to the database.

#### Usage

```java

@Component
public class AthleteDAO extends JooqDAO<Athlete, AthleteRecord, Long> {

    public AthleteDAO(DSLContext dslContext) {
        super(dslContext, ATHLETE);
    }
}
```

## License

**jooq-spring** is open and free software under Apache License, Version
2: http://www.apache.org/licenses/LICENSE-2.0.html
