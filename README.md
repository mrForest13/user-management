![Cats Friendly Badge](https://typelevel.org/cats/img/cats-badge-tiny.png) 

# User Management

An implementation of the authorization/authentication microservice using functional programming techniques. Based on scala, cats effect, doobie and http4s.

## What are we using?

I am going to work with libraries from functional programing world. However, i would rather use libraries based on cats effect.

- [Cats Effect](https://typelevel.org/cats-effect/) - as the app core
- [Http4s](https://http4s.org/) - as the web server
- [Tapir](https://tapir-scala.readthedocs.io) - for describing api http endpoints
- [Doobie](https://tpolecat.github.io/doobie/) - as database query and access library
- [Scala Cache](https://cb372.github.io/scalacache/) - cache based on redis for auth data
- [Pure Config](https://pureconfig.github.io/) - for loading configuration
- [Circe](https://circe.github.io/circe/) - for json encoding and decoding
- [Fuuid](https://christopherdavenport.github.io/fuuid/) - functional uuid
- [log4Cats](https://christopherdavenport.github.io/log4cats/) - functional logging

## How to start?

The easiest way to launch the application is to use one of the scripts prepared:

- `start-dev-env.sh` to run all required resources such as PostgresSQL or Redis as docker containers
- `start-dev-env.sh --withApp` to launch all required resources along with the application as docker containers

## Project Structure

The project is divided into five separate sub-projects.

### Core

It contains configuration case classes and some base models.

### Db

It contains dao (based on doobie), migrations (flyway) and db models.

### Service

It contains service definitions.

### Api

It contains api and swagger docs definitions.

### Application

It contains main class and all objects initialization.

## Api Documentation

You can find everything you need here (http://localhost:9000/docs) after starting the application.

## Continuous Integration

We are using git flow. Here are steps:

- `sbt clean compile`
- `sbt scalafmtCheckAll`
- `sbt scalastyle`
- `sbt coverage test`
- `sbt coverage it:test`
- `sbt coverage e2e:test`
- `sbt coverageAggregate`

## Testing

The tests are divided into three types. All in separate packages:
- unit -> package `src/test`
- integration -> package  `src/it`
- end to end -> package  `src/e2e`
