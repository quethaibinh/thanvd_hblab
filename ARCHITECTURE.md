# Clean Architecture Guide

## Current status

The project is strictly organized around **Clean Architecture** (Hexagonal Architecture) principles. It successfully separates core business logic from infrastructure concerns through a clear layering strategy and the use of ports and adapters.

## Recommended package structure

```text
src/main/java/com/example/demo
|-- DemoApplication.java
|-- domain
|   |-- model
|   `-- service
|-- application
|   |-- port
|   |   |-- in
|   |   `-- out
|   `-- service
|-- infrastructure
|   |-- adapter
|   |   |-- in
|   |   |   `-- web
|   |   `-- out
|   |       `-- persistence
|   `-- config
`-- shared
    `-- exception
```

## Layer responsibilities

- `domain`: core business rules, entities/value objects, no Spring/JPA/web dependency.
- `application`: use cases, orchestration, port interfaces.
- `infrastructure.adapter.in`: entry points such as REST controllers.
- `infrastructure.adapter.out`: technical implementations such as database access.
- `infrastructure.config`: Spring beans and wiring.

## Dependency rule

Dependencies should point inward:

```text
infrastructure -> application -> domain
```

The `domain` layer should be the most stable and independent layer.

## Practical mapping in Spring Boot

- REST controller -> `infrastructure.adapter.in.web`
- Use case interface -> `application.port.in`
- Use case implementation -> `application.service`
- Repository port -> `application.port.out`
- JPA adapter/repository/entity -> `infrastructure.adapter.out.persistence`
- Domain object -> `domain.model`

## What to avoid

- Putting `@Entity` classes in `domain` when those classes are tied to JPA concerns.
- Injecting Spring repositories directly into controllers.
- Letting `application` depend on Spring MVC, JPA, or security classes unless truly unavoidable.
- Mixing request/response DTOs with domain objects.
