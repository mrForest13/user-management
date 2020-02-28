# User Management

An implementation of the authorization/authentication microservice using functional programming techniques. Based on scala, cats effect, doobie and http4s.

## What are we using?

I am going to work with libraries from functional programing world. However, i would rather use libraries based on cats effect rather than others.

- [Cats Effect](https://typelevel.org/cats-effect/) - ast the app core
- [Http4s](https://http4s.org/) - as the web server
- [Tapir](https://tapir-scala.readthedocs.io) - for describing api http endpoints
- [Doobie](https://tpolecat.github.io/doobie/) - as database query and access library
- [Pure Config](https://pureconfig.github.io/) - for loading configuration
- [Circe](https://circe.github.io/circe/) - for json encoding and decoding
- [Fuuid](https://christopherdavenport.github.io/fuuid/) - functional uuid
- [log4Cats](https://christopherdavenport.github.io/log4cats/) - functional logging
