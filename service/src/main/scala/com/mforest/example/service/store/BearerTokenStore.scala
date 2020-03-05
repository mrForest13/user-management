package com.mforest.example.service.store

import cats.Functor.ops.toAllFunctorOps
import cats.Id
import cats.data.OptionT
import cats.effect.Async
import cats.implicits.catsSyntaxOptionId
import com.mforest.example.core.config.auth.TokenConfig
import com.mforest.example.service.formatter.TokenFormatter
import io.chrisdavenport.fuuid.FUUID
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.circe.codec
import scalacache.{Cache, CatsEffect, Flags, Mode}
import tsec.authentication.{BackingStore, TSecBearerToken}
import tsec.common.SecureRandomId

class BearerTokenStore[F[_]: Async](cache: Cache[TSecBearerToken[Id[FUUID]]], config: TokenConfig)
    extends BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]] {

  private implicit val flags: Flags  = Flags.defaultFlags
  private implicit val mode: Mode[F] = CatsEffect.modes.async[F]

  override def put(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    cache.put(token.id)(token, config.expiryDuration.some).as(token)
  }

  override def update(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    put(token)
  }

  override def delete(id: SecureRandomId): F[Unit] = {
    cache.remove(id).as(())
  }

  override def get(id: SecureRandomId): OptionT[F, TSecBearerToken[Id[FUUID]]] = OptionT {
    cache.get(id)
  }
}

object BearerTokenStore extends TokenFormatter {

  def apply[F[_]: Async](
      client: JedisPool,
      config: TokenConfig
  ): BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]] = {
    new BearerTokenStore(RedisCache(client), config)
  }
}
