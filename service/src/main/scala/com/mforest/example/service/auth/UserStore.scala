package com.mforest.example.service.auth

import cats.data.OptionT
import cats.effect.{Async, Sync}
import com.mforest.example.db.dao.UserDao
import com.mforest.example.db.row.UserRow
import doobie.syntax.ToConnectionIOOps
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import tsec.authentication.IdentityStore

class UserStore[F[_]: Sync](userDao: UserDao, transactor: Transactor[F])
    extends IdentityStore[F, FUUID, UserRow]
    with ToConnectionIOOps {

  override def get(id: FUUID): OptionT[F, UserRow] = {
    userDao.find(id).transact(transactor)
  }
}

object UserStore {

  def apply[F[_]: Async](userDao: UserDao, transactor: Transactor[F]): UserStore[F] = {
    new UserStore(userDao, transactor)
  }
}
