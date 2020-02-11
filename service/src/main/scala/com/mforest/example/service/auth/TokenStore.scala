package com.mforest.example.service.auth

import cats.data.OptionT
import cats.effect.Sync
import io.chrisdavenport.fuuid.FUUID
import tsec.authentication._
import tsec.common.SecureRandomId

import scala.collection.mutable

//FIXME move to redis or postgres
class TokenStore[F[_]](implicit F: Sync[F]) extends BackingStore[F, SecureRandomId, TSecBearerToken[FUUID]] {

  private val storageMap = mutable.HashMap.empty[SecureRandomId, TSecBearerToken[FUUID]]

  override def put(elem: TSecBearerToken[FUUID]): F[TSecBearerToken[FUUID]] = {
    if (storageMap.put(elem.id, elem).isEmpty) {
      F.pure(elem)
    } else {
      F.raiseError(new IllegalArgumentException)
    }
  }

  override def update(v: TSecBearerToken[FUUID]): F[TSecBearerToken[FUUID]] = {
    storageMap.update(v.id, v)
    F.pure(v)
  }

  override def delete(id: SecureRandomId): F[Unit] = {
    storageMap.remove(id) match {
      case Some(_) => F.unit
      case None    => F.raiseError(new IllegalArgumentException)
    }
  }

  override def get(id: SecureRandomId): OptionT[F, TSecBearerToken[FUUID]] = {
    OptionT.fromOption[F](storageMap.get(id))
  }
}

object TokenStore {

  def apply[F[_]: Sync]: TokenStore[F] = new TokenStore
}
