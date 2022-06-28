# Manifold : Simple Query
A purely experimental project to write type-safe SQL-like queries directly against POGO entity types that can target any
type of backend data source, whether it be Java Collections or SQL or what have you.
```java
Query<Person> query = Person.query((p, q) -> q
  .where(p.age >= 18 && p.gender == male)
  .orderBy(p.name));
```
Execute queries like this:
```java
Iterable<Person> result = query.run(dataSource);
```

Specify a 'select' clause using a type-safe [tuple expression](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-tuple):
```java
auto query = Person.query(p -> p
.select((p.name, DogYears: p.age * 7))
.from((s, q) -> q
  .where(p.gender == male && s.DogYears > 30)
  .orderBy(s.name)));
```
Access results as iterable [tuple](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-tuple)
values mirroring the select clause.
```java
auto result = query.run(data);

for(auto s : result) {
  System.out.println(s.name + " : " + s.DogYears);
}
```

The query API is very simple and not meant to be used for anything serious. This project primarily demonstrates usage of
the `ICompilerComponent` SPI as a means to perform Java AST transformations. It also uses other Manifold features along
the way, including extension methods, `@val`/`@var` properties, `@Self`, and others.

There are three modules comprising this project:
1. `manifold-simple-query-api` - The API classes such as `Query`, `Entity`, `Expression`, etc.
2. `manifold-simple-query-ast-transformer` - The `ICompilerComponent` implementation. Transforms query criteria expressed as normal POJO property references to a general-purpose Expression API that preserves the property reference type information. As such a wide range of data sources can be targeted e.g., POJO collections, database connections, web services, etc.
3. `manifold-simple-query-processor` - Responsible for query execution. Extends `Query` with a `run()` method and defines a simple expression evaluator, which is used internally to filter, sort, etc. queried data sources. This implementation supports simple POJO collections.

The project layout is simple and light; not a lot of code.