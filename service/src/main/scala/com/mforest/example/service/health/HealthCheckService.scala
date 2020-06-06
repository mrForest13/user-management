package com.mforest.example.service.health

import cats.Id
import cats.data.{EitherT, NonEmptyList}
import cats.effect.{Concurrent, Timer}
import com.mforest.example.core.config.health.HealthCheckConfig
import com.mforest.example.core.error.Error
import com.mforest.example.service.Service
import com.mforest.example.service.converter.DtoConverter.NonEmptyListConverter
import com.mforest.example.service.dto.HealthCheckDto
import doobie.util.transactor.Transactor
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.binary.anyRefBinaryCodec
import scalacache.{Cache, CatsEffect, Flags, Mode}
import sup.data.{HealthReporter, Tagged}
import sup.modules.doobie.connectionCheck
import sup.modules.scalacache.cached
import sup.{Health, HealthCheck, HealthResult, mods}

trait HealthCheckService[F[_]] extends Service {

  val name: String = "Health-Check-Service"

  def check: EitherT[F, Error, NonEmptyList[HealthCheckDto]]
}

class HealthCheckServiceImpl[F[_]: Concurrent: Timer](
    transactor: Transactor[F],
    cache: Cache[HealthResult[Id]],
    config: HealthCheckConfig
) extends HealthCheckService[F] {

  type TaggedCheck[A] = Tagged[String, A]

  private val flags: Flags  = Flags.defaultFlags
  private val mode: Mode[F] = CatsEffect.modes.async[F]

  def check: EitherT[F, Error, NonEmptyList[HealthCheckDto]] = EitherT.right {
    HealthReporter
      .fromChecks(databaseCheck, cacheCheck)
      .check
      .map(_.value)
      .map(_.checks)
      .map(_.to[HealthCheckDto])
  }

  private def databaseCheck: HealthCheck[F, TaggedCheck] = {
    connectionCheck(transactor)(none)
      .through(mods.recoverToSick)
      .through(mods.timeoutToSick(config.database.timeout))
      .through(mods.tagWith(config.database.name))
  }

  private def cacheCheck: HealthCheck[F, TaggedCheck] = {
    HealthCheck
      .const[F, Id](Health.Healthy)
      .through(cached[F, Id](config.cache.name, none)(cache, mode, flags))
      .through(mods.recoverToSick)
      .through(mods.timeoutToSick(config.database.timeout))
      .through(mods.tagWith(config.cache.name))
  }
}

object HealthCheckService {

  def apply[F[_]: Concurrent: Timer](
      transactor: Transactor[F],
      client: JedisPool,
      config: HealthCheckConfig
  ): HealthCheckService[F] = {
    new HealthCheckServiceImpl(transactor, RedisCache(client), config)
  }
}
