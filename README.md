# jOOQ Spring Integration

**jooq-spring** is a small open-source library that provides integration of [jOOQ](https://www.jooq.org) with
the [Spring Framework](https://spring.io/projects/spring-framework).

## Dependency

Add a dependency to the current version:

```xml

<dependency>
    <groupId>ch.martinelli.oss</groupId>
    <artifactId>jooq-spring</artifactId>
    <version>0.4.0</version>
</dependency>
```

## Components

### JooqDAO

The JooqDAO follows the [DAO pattern](https://en.wikipedia.org/wiki/Data_access_object) and not
the [Repository pattern](https://martinfowler.com/eaaCatalog/repository.html) because the Repository is a pattern from
Domain Driven Design (DDD).

#### Usage

```java

@Component
public class AthleteDAO extends JooqDAO<Athlete, AthleteRecord, Long> {

    public AthleteDAO(DSLContext dslContext) {
        super(dslContext, ATHLETE);
    }
}
```

#### Methods

| Return Type   | Method                                                                                               | Description                                                                            |
|---------------|------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| `Optional<R>` | `findById(ID id)`                                                                                    | Finds a record by its primary key.                                                     |
| `List<R>`     | `findAll(int offset, int limit, List<org.jooq.OrderField<?>> orderBy)`                               | Retrieves a list of records from the database with pagination and sorting.             |
| `List<R>`     | `findAll(org.jooq.Condition condition, int offset, int limit, List<org.jooq.OrderField<?>> orderBy)` | Retrieves a list of records from the database with filtering, pagination, and sorting. |
| `List<R>`     | `findAll(org.jooq.Condition condition, List<org.jooq.OrderField<?>> orderBy)`                        | Retrieves a list of records from the database with filtering, and sorting.             |
| `List<R>`     | `findAll(org.jooq.Condition condition)`                                                              | Retrieves a list of records from the database with filtering.                          |
| `int`         | `count()`                                                                                            | Counts the total number of records in the associated table.                            |
| `int`         | `count(org.jooq.Condition condition)`                                                                | Counts the number of records in the associated table that match the given condition.   |
| `int`         | `save(R record)`                                                                                     | Saves the given record to the database.                                                |
| `int[]`       | `saveAll(List<R> record)`                                                                            | Saves a list of records to the database using batch store operations.                  |
| `int`         | `merge(R record)`                                                                                    | Merges the given record into the database.                                             |
| `int`         | `deleteById(ID id)`                                                                                  | Deletes a record from the database identified by its primary key.                      |
| `int`         | `delete(R record)`                                                                                   | Deletes the specified record from the database.                                        |
| `int`         | `delete(org.jooq.Condition condition)`                                                               | Deletes records from the database that match the given condition.                      |

Check out the code documentation for further information [JooqDAO](src/main/java/ch/martinelli/oss/jooqspring/JooqDAO.java).

## License

**jooq-spring** is open and free software under Apache License, Version
2: http://www.apache.org/licenses/LICENSE-2.0.html
