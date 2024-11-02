# jOOQ Spring Integration

**jooq-spring** is a small open source library that provides inteegation of [jOOQ](https://www.jooq.org) with the [Spring Framework](https://spring.io/projects/spring-framework).

## Dependency 

Add a dependency to the current version:

```xml
<dependency>
    <groupId>ch.martinelli.oss</groupId>
    <artifactId>jooq-spring</artifactId>
    <version>0.0.3</version>
</dependency>
```

## Components
### JooqRepository

Checkout the code for documentation [JooqRepository](src/main/java/ch/martinelli/oss/jooqspring/JooqRepository.java)

#### Usage
```java
@Repository
public class AthleteRepository extends JooqRepository<Athlete, AthleteRecord, Long> {

    public AthleteRepository(DSLContext dslContext) {
        super(dslContext, ATHLETE);
    }
}
```

## License
**jooq-spring** is open and free software under Apache License, Version 2: http://www.apache.org/licenses/LICENSE-2.0.html
