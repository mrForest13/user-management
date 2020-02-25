package com.mforest.example.service.store

import cats.Functor.ops.toAllFunctorOps
import cats.Id
import cats.data.{NonEmptyChain, OptionT}
import cats.effect.Sync
import com.mforest.example.db.dao.PermissionDao
import com.mforest.example.service.converter.DtoConverter.DtoChainConverter
import com.mforest.example.service.dto.PermissionDto
import doobie.syntax.ToConnectionIOOps
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import tsec.authentication.IdentityStore

class PermissionsStore[F[_]: Sync](dao: PermissionDao, transactor: Transactor[F])
    extends IdentityStore[F, Id[FUUID], NonEmptyChain[PermissionDto]]
    with ToConnectionIOOps {

  override def get(userId: Id[FUUID]): OptionT[F, NonEmptyChain[PermissionDto]] = OptionT {
    dao
      .findByUser(userId)
      .transact(transactor)
      .map(_.to[PermissionDto])
      .map(NonEmptyChain.fromChain)
  }
}

object PermissionsStore {

  def apply[F[_]: Sync](
      dao: PermissionDao,
      transactor: Transactor[F]
  ): IdentityStore[F, Id[FUUID], NonEmptyChain[PermissionDto]] = {
    new PermissionsStore(dao, transactor)
  }
}
