# jOOQ Spring Integration

**jooq-spring** is a small open-source library that provides integration of [jOOQ](https://www.jooq.org) with the [Spring Framework](https://spring.io/projects/spring-framework).
It follows the [DAO pattern](https://en.wikipedia.org/wiki/Data_access_object) and not the Repository pattern 
because the Repository is a pttern

## Dependency 

Add a dependency to the current version:

```xml
<dependency>
    <groupId>ch.martinelli.oss</groupId>
    <artifactId>jooq-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Components
### JooqRepository

Checkout the code for documentation [JooqRepository](src/main/java/ch/martinelli/oss/jooqspring/JooqDAO.java)

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
**jooq-spring** is open and free software under Apache License, Version 2: http://www.apache.org/licenses/LICENSE-2.0.html
