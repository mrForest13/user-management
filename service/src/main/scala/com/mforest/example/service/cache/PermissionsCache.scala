package com.mforest.example.service.cache

import scalacache._
import scalacache.redis._
import scalacache.serialization.binary._

class PermissionsCache {

  import cats.effect.IO
  import scalacache.Mode

  implicit val mode: Mode[IO] = scalacache.CatsEffect.modes.async

  implicit val redisCache: Cache[String] = RedisCache("host1", 6379)

}
