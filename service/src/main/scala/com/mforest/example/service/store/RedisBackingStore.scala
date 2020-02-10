package com.mforest.example.service.store

import cats.data.OptionT
import tsec.authentication.BackingStore

class RedisBackingStore[F[_], I, V] extends BackingStore[F, I, V] {

  override def put(elem: V): F[V] = ???

  override def update(v: V): F[V] = ???

  override def delete(id: I): F[Unit] = ???

  override def get(id: I): OptionT[F, V] = ???
}
