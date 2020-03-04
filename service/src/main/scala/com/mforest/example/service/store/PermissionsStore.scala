package com.mforest.example.service.store

import cats.Functor.ops.toAllFunctorOps
import cats.Id
import cats.data.{Chain, NonEmptyChain, OptionT}
import cats.effect.Async
import cats.syntax.OptionSyntax
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.converter.DtoConverter.ChainConverter
import com.mforest.example.service.dto.PermissionDto
import doobie.syntax.ToConnectionIOOps
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import redis.clients.jedis.JedisPool
import scalacache.redis.RedisCache
import scalacache.serialization.circe.codec
import scalacache.{Cache, CatsEffect, Flags, Mode}
import tsec.authentication.IdentityStore

class PermissionsStore[F[_]: Async](dao: PermissionDao, cache: Cache[Chain[PermissionDto]], transactor: Transactor[F])
    extends IdentityStore[F, Id[FUUID], NonEmptyChain[PermissionDto]]
    with ToConnectionIOOps
    with OptionSyntax {

  private implicit val flags: Flags  = Flags.defaultFlags
  private implicit val mode: Mode[F] = CatsEffect.modes.async[F]

  override def get(userId: Id[FUUID]): OptionT[F, NonEmptyChain[PermissionDto]] = OptionT {
    getOrLoad(userId).map(NonEmptyChain.fromChain)
  }

  private def getOrLoad(userId: Id[FUUID]): F[Chain[PermissionDto]] = {
    cache.cachingForMemoizeF(userId.show)(none) {
      dao
        .findByUser(userId)
        .transact(transactor)
        .map(_.to[PermissionDto])
    }
  }
}

object PermissionsStore {

  def apply[F[_]: Async](
      dao: PermissionDao,
      client: JedisPool,
      transactor: Transactor[F]
  ): IdentityStore[F, Id[FUUID], NonEmptyChain[PermissionDto]] = {
    new PermissionsStore(dao, RedisCache(client), transactor)
  }
}
