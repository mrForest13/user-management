package com.mforest.example.db.cache

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Resource, Sync}
import cats.implicits.toFlatMapOps
import com.mforest.example.core.config.db.RedisConfig
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{JedisPool, JedisPoolConfig, Protocol}

final class Cache[F[_]: Sync](config: RedisConfig) {

  private val startMsg = (size: Int) => s"Starting pool with max active instances: $size"
  private val closeMsg = (size: Int) => s"Closing pool with active instances: $size"

  def pool(): Resource[F, JedisPool] = {
    Resource.make(client(new JedisPoolConfig))(close)
  }

  private def client(poolConfig: GenericObjectPoolConfig): F[JedisPool] = Sync[F].suspend {
    Slf4jLogger
      .create[F]
      .flatMap(_.info(startMsg(poolConfig.getMaxTotal)))
      .as {
        new JedisPool(
          poolConfig,
          config.host,
          config.port,
          Protocol.DEFAULT_TIMEOUT,
          config.password.orNull
        )
      }
  }

  private def close(pool: JedisPool): F[Unit] = Sync[F].suspend {
    Slf4jLogger
      .create[F]
      .flatMap(_.info(closeMsg(pool.getNumActive)))
      .as(pool.close())
  }
}

object Cache {

  def apply[F[_]: Sync](config: RedisConfig): Cache[F] = new Cache(config)
}
