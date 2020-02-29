package com.mforest.example.service.store

import java.time.Instant

import cats.Functor.ops.toAllFunctorOps
import cats.Id
import cats.data.OptionT
import cats.effect.Async
import cats.syntax.OptionSyntax
import com.mforest.example.core.formatter.{FuuidFormatter, InstantFormatter}
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.circe.codec
import scalacache.{Cache, CatsEffect, Mode}
import tsec.authentication.{BackingStore, TSecBearerToken}
import tsec.common.SecureRandomId

import scala.concurrent.duration.Duration

class BarerTokenStore[F[_]: Async](cache: Cache[TSecBearerToken[Id[FUUID]]])
    extends BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]]
    with OptionSyntax {

  implicit val mode: Mode[F] = CatsEffect.modes.async[F]

  override def put(token: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    cache.put(token.id)(token, ttl(token).some).as(token)
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

  private def ttl(token: TSecBearerToken[Id[FUUID]]): Duration = {
    Duration.fromNanos((token.expiry.getNano - Instant.now.getNano).toLong)
  }
}

object BarerTokenStore extends FuuidFormatter with InstantFormatter {

  def apply[F[_]: Async](client: JedisPool): BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]] = {
    new BarerTokenStore(RedisCache(client))
  }

  implicit val encoder1: Encoder[SecureRandomId] = Encoder.encodeString.asInstanceOf[Encoder[SecureRandomId]]
  implicit val decoder1: Decoder[SecureRandomId] = Decoder.decodeString.asInstanceOf[Decoder[SecureRandomId]]

  implicit val encoder: Encoder[TSecBearerToken[Id[FUUID]]] = deriveEncoder
  implicit val decoder: Decoder[TSecBearerToken[Id[FUUID]]] = deriveDecoder
}
