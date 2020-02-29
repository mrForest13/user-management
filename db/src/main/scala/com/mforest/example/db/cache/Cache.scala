package com.mforest.example.db.cache

import cats.effect.{Resource, Sync}
import com.mforest.example.core.config.db.RedisConfig
import redis.clients.jedis.{JedisPool, JedisPoolConfig}

class Cache[F[_]: Sync](config: RedisConfig) {

  def pool: Resource[F, JedisPool] = {
    val alloc = Sync[F].delay(client)
    val free  = (pool: JedisPool) => Sync[F].delay(pool.close())

    Resource.make(alloc)(free)
  }

  private def client: JedisPool = {
    new JedisPool(new JedisPoolConfig, config.host, config.port)
  }
}

object Cache {

  def apply[F[_]: Sync](config: RedisConfig): Cache[F] = new Cache(config)
}
