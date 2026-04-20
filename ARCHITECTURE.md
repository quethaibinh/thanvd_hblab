# Clean Architecture Guide

## Current assessment

The project started from the default Spring Boot scaffold, so it was not yet organized around Clean Architecture layers.

That is not a violation by itself, but if new features are added directly under `controller`, `service`, `repository`, and `entity` packages, the codebase will quickly become framework-centric instead of business-centric.

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
