---
inclusion: fileMatch
fileMatchPattern: "**/*.java"
---

# JPA & Spring Boot Conventions

## When writing Entity classes:
- Always add `@Entity` and `@Table(name = "...")` annotations
- Use `@Id` and `@GeneratedValue` for primary keys
- Document each relationship annotation with a comment explaining what it does
- Use `@Column` to explicitly name columns when clarity helps

## When writing Repository interfaces:
- Extend `JpaRepository<EntityType, IdType>`
- Add custom query methods using Spring Data naming conventions
- Add `@Query` annotation for complex queries with a comment explaining the SQL

## When writing Service classes:
- Always annotate with `@Service`
- Use `@Transactional` for methods that modify multiple entities
- Add comments explaining WHY a transaction boundary is needed

## When writing Controller classes:
- Use `@RestController` and `@RequestMapping`
- Return `ResponseEntity` for proper HTTP status codes
- Keep controllers thin – delegate logic to services
