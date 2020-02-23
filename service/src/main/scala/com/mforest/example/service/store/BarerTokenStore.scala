package com.mforest.example.service.store

import cats.Id
import cats.data.OptionT
import cats.effect.Sync
import io.chrisdavenport.fuuid.FUUID
import tsec.authentication._
import tsec.common.SecureRandomId

import scala.collection.mutable

//FIXME move to redis or postgres
class BarerTokenStore[F[_]](implicit F: Sync[F]) extends BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]] {

  private val storageMap = mutable.HashMap.empty[SecureRandomId, TSecBearerToken[Id[FUUID]]]

  override def put(elem: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    if (storageMap.put(elem.id, elem).isEmpty) {
      F.pure(elem)
    } else {
      F.raiseError(new IllegalArgumentException)
    }
  }

  override def update(v: TSecBearerToken[Id[FUUID]]): F[TSecBearerToken[Id[FUUID]]] = {
    storageMap.update(v.id, v)
    F.pure(v)
  }

  override def delete(id: SecureRandomId): F[Unit] = {
    storageMap.remove(id) match {
      case Some(_) => F.unit
      case None    => F.raiseError(new IllegalArgumentException)
    }
  }

  override def get(id: SecureRandomId): OptionT[F, TSecBearerToken[Id[FUUID]]] = {
    OptionT.fromOption[F](storageMap.get(id))
  }
}

object BarerTokenStore {

  def apply[F[_]: Sync]: BackingStore[F, SecureRandomId, TSecBearerToken[Id[FUUID]]] = new BarerTokenStore
}
